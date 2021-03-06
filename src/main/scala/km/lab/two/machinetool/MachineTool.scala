package km.lab.two.machinetool

import scala.collection.mutable
import km.lab.two.detail.Detail
import org.dyndns.phpusr.util.log.Logger
import java.util.concurrent.atomic.AtomicBoolean
import km.lab.two.constants.Const
import org.dyndns.phpusr.util.stat.Stat
import java.util.{TimerTask, Timer}

/**
 * @author phpusr
 *         Date: 08.05.14
 *         Time: 12:46
 */

/**
 * Станок
 */
class MachineTool(name: String) {
  /** Включен или выключен станок */
  private val enable = new AtomicBoolean(false)

  /** Очередь деталей */
  private val detailQueue = mutable.Queue[Detail]()

  /** Действие над обработанной деталью */
  private var action: (Detail, Boolean) => Unit = null

  /** Logger */
  private val logger = new Logger(true, true, true)

  /** Состояние занятости станка */
  private val busyState = new AtomicBoolean(false)
  private def buzyStateInt = if (busyState.get) 1 else 0

  /** Обработчик деталей */
  private val handler = new Thread(new Runnable {
    override def run() {
      while (enable.get) {
        detailQueue.synchronized {
          if (detailQueue.nonEmpty && !busyState.get) {
            busyState.set(true)
            val currentDetail = detailQueue.dequeue()
            logger.debug(s"${MachineTool.this} processes $currentDetail")
            currentDetail.operation { v => busyState.set(false) }
            action(currentDetail, false)
          }
        }
        Thread.sleep(Const.ThreadSleepMilis)
      }
    }
  })

  /** Средняя загрузка станка */
  private val avgLoad = new Stat

  //--------------------------------------------------

  // Подсчет средней загрузки станка
  new Timer().schedule(new TimerTask() {
    override def run() {
      avgLoad.newElement()
      avgLoad.add(detailQueue.size + buzyStateInt)
    }
  }, 0, 1000)

  /** Добавление детали в очередь */
  def addDetail(detail: Detail) = synchronized {
    logger.debug(s"$this add $detail")
    detailQueue += detail
  }

  /** Запуск станка */
  def start() {
    logger.debug(s"Start $this")
    enable.set(true)
    handler.start()
  }

  /** Остановка станка */
  def stop() {
    logger.debug(s"Stop $this")
    enable.set(false)
  }

  override def toString = s"MachineTool($name)"
}

/**
 * Станки участка цеха
 */
object MachineTool {
  def apply(name: String) = new MachineTool(name)

  val A1 = MachineTool("A1")
  val A2 = MachineTool("A2")
  val A3 = MachineTool("A3")
  
  private val list = List(A1, A2, A3)

  /** Установить действие над обработанной деталью всем станкам */
  def setAction(action: (Detail, Boolean) => Unit) = list.foreach(_.action = action)

  /** Запустить все станки */
  def startAll() = list.foreach(_.start())

  /** Остановить все станки */
  def stopAll() = list.foreach(_.stop())

  /** Размеры очередей на станках */
  def detailQueueSize = list.map(e => (e.detailQueue.size, e.buzyStateInt, e.avgLoad.avg))
}
