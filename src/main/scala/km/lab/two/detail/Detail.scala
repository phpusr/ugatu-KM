package km.lab.two.detail

import scala.collection.immutable.Queue

/**
 * @author phpusr
 *         Date: 08.05.14
 *         Time: 13:03
 */

/**
 * Тип детали
 */
case class DetailType(name: String, operations: Seq[Operation])

/**
 * Типы деталей
 */
object DetailType {
  import km.lab.two.detail.{Operation => Op}

  /** Деталь 1-о типа */
  val V1 = DetailType("One", Seq(Op._1, Op._2, Op._3))
  /** Деталь 2-о типа */
  val V2 = DetailType("Two", Seq(Op._3, Op._4, Op._5))
}

/**
 * Деталь
 */
case class Detail(name: String, detailType: DetailType) {
  /** Очередь операций наод деталью */
  private val operationQueue = new Queue[Operation](detailType.operations)

  /** Выполнить операцию над деталью из очереди */
  def operation() {
    val op = operationQueue.dequeue._1
    val handlingTime = op.handlingTime.get
    Thread.sleep(handlingTime)
  }

  /** Текущая операция */
  def currentOperation = if (operationQueue.nonEmpty) Option(operationQueue.head) else None

  override def toString = s"$name type ${detailType.name}"
}
