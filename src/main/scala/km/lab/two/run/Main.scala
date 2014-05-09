package km.lab.two.run

import km.lab.two.timeslot.Timeslot
import km.lab.two.detail.{DetailGenerator, DetailType, Detail}
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import org.dyndns.phpusr.util.log.Logger
import km.lab.two.machinetool.MachineTool

/**
 * @author phpusr
 *         Date: 08.05.14
 *         Time: 14:06
 */

object Main {

  /** Ускорение времени */
  val Acceleration = 200

}

/**
 * TODO
 */
class Main extends Thread {

  /** Ускорение времени */
  val Acceleration = 200

  /** Очередь необработанных деталей */
  //TODO сделать потокобезопасной
  val detailQueue = mutable.Queue[Detail]()

  /** Склад обработанных деталей */
  val warehouse = ListBuffer[Detail]()

  /** Logger */
  val logger = new Logger(true, true, false)

  /** Всего сгенерировано деталей */
  // TODO readonly
  var generateDetailCount = 0

  /** Добавление детали в очередь */
  val addDetailToQueue = (detail: Detail) => {
    logger.debug(s"Add detail to queue: $detail")
    generateDetailCount += 1
    detailQueue += detail
    print("") //TODO Для того, чтобы тип был Unit
  }

  // Поток деталей 1-го типа
  val incomingIntervalTypeOne = Timeslot(15, 5)
  val generatorV1 = new DetailGenerator(DetailType.V1, addDetailToQueue, incomingIntervalTypeOne)
  // Поток деталей 2-го типа
  val incomingIntervalTypeTwo = Timeslot(35, 8)
  val generatorV2 = new DetailGenerator(DetailType.V2, addDetailToQueue, incomingIntervalTypeTwo)

  override def run() {
    generatorV1.start()
    generatorV2.start()

    // Запуск станков
    MachineTool.setAction(addDetailToQueue)
    MachineTool.startAll()

    // Размещение деталей по станкам
    while(true) {
      if (detailQueue.nonEmpty) {
        val detail = detailQueue.dequeue()
        val currentOperation = detail.currentOperation

        // Если есть следующая операция, то помещаем деталь в очередь станка, выполняющего эту операцию
        if (currentOperation.isDefined) {
          val machineTool = currentOperation.get.machineTool
          logger.debug(s"Detail: $detail add to $machineTool")
          //TODO сделать потокобезопасным
          machineTool.addDetail(detail)
        }
        // Если нет, значти деталь обработана полностью, отправляем ее на склад
        else addToWarehouse(detail)
      } else {
        logger.trace("detailQueue empty")
        Thread.sleep(500)
      }
    }
  }

  /** Остановка поставки деталей */
  def stopGenerateDetail() {
    //TODO not work
    generatorV1.stop()
    generatorV2.stop()
  }

  /** Добавить деталь на склад */
  def addToWarehouse(detail: Detail) {
    logger.debug(s"Add detail to warehouse: $detail")
    warehouse += detail
  }

  /** Размеры очередей на станках */
  def warehouseDetailQueueSize = MachineTool.detailQueueSize

}
