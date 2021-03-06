package km.rgr.minimarket

import java.util.concurrent.atomic.AtomicBoolean
import org.dyndns.phpusr.util.log.Logger
import km.rgr.minimarket.constants.Const

/**
 * @author phpusr
 *         Date: 15.05.14
 *         Time: 13:53
 */

/**
 * Генератор покупателей
 */
class CustomerGenerator(action: Customer => Unit) {

  private val logger = new Logger(true, true, true)

  private val enable = new AtomicBoolean(false)

  /** Перерыв между заходами покупателей */ //TODO переделать под Пуассона
  private val generationTimeout = Const.CustomerGenerationTimeout

  /** Поток генерации покупателей */
  private val thread = new Thread(new Runnable {
    override def run() {
      var customerIndex = 0
      while (enable.get) {
        customerIndex += 1
        Thread.sleep(generationTimeout.get)

        val customer = Customer("Customer-" + customerIndex)
        logger.debug("CustomerGenerator generate: " + customer)

        action(customer)
      }
    }
  })

  def start() {
    logger.debug("CustomerGenerator start")
    enable.set(true)
    thread.start()
  }

  def stop() {
    logger.debug("CustomerGenerator stop")
    enable.set(false)
  }

}
