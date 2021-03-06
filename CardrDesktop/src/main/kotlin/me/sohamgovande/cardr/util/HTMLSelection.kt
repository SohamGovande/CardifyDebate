@file:Suppress("DEPRECATION")

package me.sohamgovande.cardr.util

import org.apache.logging.log4j.LogManager
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable
import java.awt.datatransfer.UnsupportedFlavorException
import java.io.InputStream
import java.io.Reader
import java.io.StringBufferInputStream
import java.io.StringReader
import java.util.ArrayList

class HTMLSelection(private val html: String): Transferable {

    override fun getTransferDataFlavors(): Array<DataFlavor> {
        return htmlFlavors.toTypedArray()
    }

    override fun isDataFlavorSupported(flavor: DataFlavor): Boolean {
        return htmlFlavors.contains(flavor)
    }

    @Throws(UnsupportedFlavorException::class)
    override fun getTransferData(flavor: DataFlavor): Any {
        if (String::class.java == flavor.representationClass) {
            return html
        } else if (Reader::class.java == flavor.representationClass) {
            return StringReader(html)
        } else if (InputStream::class.java == flavor.representationClass) {
            return StringBufferInputStream(html)
        }
        throw UnsupportedFlavorException(flavor)
    }

    companion object {
        private val htmlFlavors = ArrayList<DataFlavor>()
        val logger = LogManager.getLogger(HTMLSelection::class.java)

        init {
            try {
                htmlFlavors.add(DataFlavor("text/html;class=java.lang.String"))
                htmlFlavors.add(DataFlavor("text/html;class=java.io.Reader"))
                htmlFlavors.add(DataFlavor("text/html;charset=unicode;class=java.io.InputStream"))
            } catch (ex: ClassNotFoundException) {
                logger.error("Unable to add HTMLSelection class", ex)
            }
        }
    }

}