package me.sohamgovande.cardr.core.ui.tabs

import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.effect.ColorAdjust
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.VBox
import me.sohamgovande.cardr.core.ui.CardrUI
import me.sohamgovande.cardr.data.prefs.Prefs
import java.io.InputStream

abstract class TabUI(tabName: String, val cardrUI: CardrUI) {

    var isAlive = true
    protected val panel = VBox()
    val internalTab = Tab(tabName)
    protected val onCloseListeners = mutableListOf<() -> Unit>()

    abstract fun generate()

    fun addToTabPane(tabPane: TabPane, addedPostInit: Boolean) {
        internalTab.content = panel
        internalTab.setOnClosed {
            onClose()
            for (listener in onCloseListeners)
                listener()
        }
        internalTab.setOnSelectionChanged {
            if (internalTab.isSelected)
                onTabSelected()
        }
        tabPane.tabs.add(tabPane.tabs.size + if (addedPostInit) -1 else 0,  internalTab)
    }

    fun onClose() {
        isAlive = false
        cardrUI.tabs.remove(this)
    }

    open fun onTabSelected() {}
    open fun doDeferredLoad() {}
    open fun loadIcons() {}
    open fun onWindowResized() {}

    companion object {
        @JvmStatic
        fun loadMiniIcon(path: String, overrideDarkMode: Boolean, scale: Double): ImageView? {
            val copyResource: InputStream? = TabUI::class.java.getResourceAsStream(path)
            if (copyResource != null) {
                val image = Image(copyResource, 15.0 * scale, 15.0 * scale, true, true)
                val imageView = ImageView(image)
                if (Prefs.get().darkMode || overrideDarkMode) {
                    if (!path.contains("folder")) {
                        val effect = ColorAdjust()
                        effect.brightness = 1.0
                        imageView.effect = effect
                    }
                }
                return imageView
            }
            return null
        }
    }
}