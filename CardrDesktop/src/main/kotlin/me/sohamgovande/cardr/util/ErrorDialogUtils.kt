package me.sohamgovande.cardr.util

import javafx.application.Platform
import javafx.scene.control.Alert
import javafx.scene.control.ButtonBar
import javafx.scene.control.ButtonType
import javafx.scene.layout.Region
import me.sohamgovande.cardr.CardrDesktop
import me.sohamgovande.cardr.data.prefs.Prefs
import java.awt.Desktop
import java.io.ByteArrayOutputStream
import java.io.PrintWriter
import java.io.UnsupportedEncodingException
import java.net.URI
import java.net.URLEncoder
import kotlin.system.exitProcess

fun showErrorDialogUnblocking(brief: String, full: String) {
    Platform.runLater {
        showErrorDialogBlocking(brief, full)
    }
}

fun showErrorDialogBlocking(brief: String, full: String) {
    val alert = Alert(Alert.AlertType.ERROR)
    alert.dialogPane.stylesheets.add(CardrDesktop::class.java.getResource(Prefs.get().getStylesheet()).toExternalForm())
    alert.title = "Error"
    alert.headerText = brief
    alert.contentText = full
    alert.dialogPane.minHeight = Region.USE_PREF_SIZE
    alert.showAndWait()
}

fun showInfoDialogUnblocking(brief: String, full: String) {
    Platform.runLater {
        showInfoDialogBlocking(brief, full)
    }
}

fun showInfoDialogBlocking(brief: String, full: String) {
    val alert = Alert(Alert.AlertType.INFORMATION)
    alert.dialogPane.stylesheets.add(CardrDesktop::class.java.getResource(Prefs.get().getStylesheet()).toExternalForm())
    alert.title = "Message"
    alert.headerText = brief
    alert.contentText = full
    alert.dialogPane.minHeight = Region.USE_PREF_SIZE
    alert.showAndWait()
}

fun showInfoDialogBlocking(brief: String, full: String, primaryOption: String, action: () -> Unit) {
    val primaryBT = ButtonType(primaryOption, ButtonBar.ButtonData.OK_DONE)
    val exitBT = ButtonType("OK", ButtonBar.ButtonData.CANCEL_CLOSE)

    val alert = Alert(Alert.AlertType.INFORMATION, "", primaryBT, exitBT)
    alert.dialogPane.stylesheets.add(CardrDesktop::class.java.getResource(Prefs.get().getStylesheet()).toExternalForm())
    alert.title = "Message"
    alert.headerText = brief
    alert.contentText = full
    alert.dialogPane.minHeight = Region.USE_PREF_SIZE
    val result = alert.showAndWait()
    if (result.isPresent && result.get() == primaryBT) {
        action()
    }
}

fun showInfoDialogUnblocking(brief: String, full: String, primaryOption: String, action: () -> Unit) {
    Platform.runLater {
        showInfoDialogBlocking(brief, full, primaryOption, action)
    }
}

private fun urlEncode(str: String): String {
    return try {
        URLEncoder.encode(str, "UTF-8").replace("+", "%20")
    } catch (e: UnsupportedEncodingException) {
        throw RuntimeException(e)
    }
}

fun sendToDeveloper(msg: String, emailSubject: String) {
    val desktop = Desktop.getDesktop()
    val message = "mailto:sohamthedeveloper@gmail.com?" +
        "subject=${urlEncode(emailSubject)}" +
        "&body=${urlEncode(msg)}"
    val uri = URI.create(message)
    desktop.mail(uri)
}

fun showErrorDialog(e: Exception) {
    val sendBT = ButtonType("Send Log File", ButtonBar.ButtonData.OK_DONE)
    val continueBT = ButtonType("Continue", ButtonBar.ButtonData.OK_DONE)
    val exitBT = ButtonType("Exit", ButtonBar.ButtonData.CANCEL_CLOSE)

    val alert = Alert(Alert.AlertType.ERROR, "", sendBT, continueBT, exitBT)
    alert.dialogPane.stylesheets.add(CardrDesktop::class.java.getResource(Prefs.get().getStylesheet()).toExternalForm())
    alert.headerText = "Uh-oh! There was an error."
    alert.contentText = "We encountered an unknown error of type ${e.javaClass.simpleName}: ${e.message}. We apologize for the inconvenience. Please kindly select \"Send Log File\" to share the error log with the developers so that they can fix the bug or help you out. We apologize for the inconvenience."
    alert.dialogPane.minHeight = Region.USE_PREF_SIZE
    val result = alert.showAndWait()
    var forceClose = true
    if (result.isPresent && result.get() == sendBT) {
        val baos = ByteArrayOutputStream()
        val writer = PrintWriter(baos)
        e.printStackTrace(writer)
        writer.close()

        sendToDeveloper(baos.toString(), "Cardr Error")
    }
    if (result.isPresent && result.get() == continueBT) {
        forceClose = false
    }
    if (forceClose) {
        exitProcess(0)
    }
}
