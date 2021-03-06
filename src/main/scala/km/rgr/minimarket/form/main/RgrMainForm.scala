package km.rgr.minimarket.form.main

import scala.swing._
import km.rgr.minimarket.MiniMarket
import javax.swing.Timer
import java.awt.event.{ActionEvent, ActionListener}
import km.rgr.minimarket.constants.Const

/**
 * @author phpusr
 *         Date: 15.05.14
 *         Time: 16:16
 */

/**
 * Главаная форма для РГР
 */
object RgrMainForm extends SimpleSwingApplication {

  private val infoTextArea = new TextArea(15, 30)

  private val market = new MiniMarket

  private val stopCustomerGeneratorButton = new Button(Action("Stop customer") {
    market.stopCustomerGenerator()
  })

  def top = new MainFrame {
    contents = new GridBagPanel {
      val c = new Constraints

      layout(infoTextArea) = c

      c.gridy = 1
      layout(stopCustomerGeneratorButton) = c
    }

    centerOnScreen()
  }


  miniMarket()

  def miniMarket() {

    market.start()

    val floatFormat = "%.2f"

    // Таймер обновления информации о магазине
    new Timer(100, new ActionListener {
      override def actionPerformed(e: ActionEvent) {
        val info = market.getInfo
        infoTextArea.text =
          s"\nКол-во часов работы: ${millisToHours(market.workTime)}" +
          s"\nВсего зашло: ${info.customerCount}" +
          s"\nПокупатели выбирающие товары: ${info.notServiceCustomerCount}" +
          s"\nВ кассе: ${info.customerServiceNowCount}" +
          s"\nОчередь у кассы: ${info.queueLength}" +
          s"\nОбслуженные покупатели: ${info.serviceCustomerCount}" +
          s"\n-----------------------------------" +
          s"\nВероятность простоя кассира: ${info.pCashierDownTime formatted floatFormat}" +
          s"\nСредняя длина очереди: ${info.avgQueueLength formatted floatFormat}" +
          s"\nСреднее число покупателей: ${info.avgCustomerCount.toLong}" +
          s"\nСреднее время ожидания обслуживания: ${millisToMinutes(info.avgWaitServiceTime.toLong)}" +
          s"\nСреднее время пребывания: ${millisToMinutes(info.avgStayTime.toLong)}"
      }
    }).start()


  }

  /** мс. -> мин. (с учетом ускорения времени) */
  private def millisToMinutes(milis: Long) = Math.round(milis.toFloat * Const.Acceleration / 1000 / 60).toString + " мин."

  /** мс. -> ч. (с учетом ускорения времени) */
  private def millisToHours(milis: Long) = Math.round(milis.toFloat * Const.Acceleration / 1000 / 60 / 60).toString + " ч."

}
