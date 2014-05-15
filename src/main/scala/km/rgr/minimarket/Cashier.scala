package km.rgr.minimarket

import km.rgr.minimarket.timeslot.Timeslot

/**
 * @author phpusr
 *         Date: 15.05.14
 *         Time: 13:52
 */

/**
 * Кассир
 */
case class Cashier() {

  /** Свободен или занят */
  private var _free = true

  /** Интервал времени обслуживания */
  private val serviceTime = Timeslot(1, 10)

  /** Свободен ли кассир */
  def free: Boolean = _free

  /** Обслужить покупателя */
  def serviceCustomer(customer: Customer) {
    assert(free)

    _free = false
    Thread.sleep(serviceTime.get)
    _free = true
  }

}