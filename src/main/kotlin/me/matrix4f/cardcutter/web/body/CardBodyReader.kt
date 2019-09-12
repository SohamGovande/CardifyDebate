package me.matrix4f.cardcutter.web.body

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.util.*

/**
 * Tailored to:
 * - POLITICO
 * - Reuters
 * - NY Post
 * - Al Jazeera
 * - Vox
 * - Washington Post
 *
 */
class CardBodyReader(private val hostName: String, private val doc: Document) {

    private fun businessinsider(): Elements {
        return doc.select("div[data-piano-inline-content-wrapper] p")
    }

    private fun politico(): Elements {
        val a = doc.select(".story-text p").filter {
            it.parent().tagName()!="figcaption" && it.classNames().size == 0 && !it.text().contains("Missing out on the latest scoops?")
        }
        return Elements(a)
    }

    private fun thinkprogress(): Elements {
        return doc.select(".post__content p")
    }

    private fun sciencemag(): Elements {
        return doc.select(".article__body p")
    }

    private fun newyorkpost(): Elements {
        val a = doc.select(".entry-content p").filter {
            !it.parent().hasClass("thankyou") &&
                !it.parent().id().equals("footer-legal") &&
                !it.hasClass("byline") &&
                !it.hasClass("byline-date") &&
                !it.hasClass("read-next") &&
                !it.hasClass("read-next-link") &&
                !it.hasClass("share-count")
        }
        return Elements(a)
    }

    private fun delawareonline(): Elements {
        return doc.getElementsByClass(".p-text")
    }

    private fun aljazeera(): Elements {
        val a = doc.select(".article-p-wrapper p").filter {
            !it.text().contains("The views expressed in this article are")
        }
        return Elements(a)
    }

    private fun wthr(): Elements {
        return doc.select(".field-items p")
    }

    private fun washingtonexaminer(): Elements {
        return doc.select(".RichTextArticleBody-body p")
    }

    private fun newyorker(): Elements {
        return doc.select(".SectionBreak p")
    }

    private fun vox(): Elements {
        val a = doc.select("" +
            "div.c-entry-content p, " +
            "div.c-entry-content blockquote, " +
            "div.c-entry-content p, " +
            "div.c-entry-content h3, " +
            "div.c-entry-content ul, " +
            "div.c-entry-content ol")

        return Elements(a)
    }

    private fun cnn(): Elements { // Doesn't work - CNN waits to load the paragraphs
        return doc.getElementsByClass("zn-body__paragraph")
    }

    private fun bbc(): Elements {
        return doc.select(".story-body__inner p")
    }

    private fun thehill(): Elements {
        return doc.select(".field-name-body .field-items")
    }

    private fun reuters(): Elements {
        val a = doc.select(".StandardArticleBody_body p").filter {
            !it.hasClass("Attribution_content")
        }
        return Elements(a)
    }

    private fun nytimes(): Elements {
        return doc.select(".StoryBodyCompanionColumn")
    }

    private fun foxnews(): Elements {
        val a = doc.select(".article-body p").filter {
            if (it.children().size > 0) {
                if (it.child(0).tagName().equals("strong"))
                    return@filter false
            }
            return@filter true
        }
        return Elements(a)
    }

    private fun theverge(): Elements {
        return doc.select(".c-entry-content p, .c-entry-content h2")
    }

    private fun wsj(): Elements {
        return doc.select(".wsj-snippet-body p")
    }

    private fun engadget(): Elements {
        return doc.select(".o-article_block p")
    }

    private fun bgr(): Elements {
        return doc.select(".entry-content p")
    }

    private fun aol(): Elements {
        return doc.select("#article-content p")
    }

    private fun dailywire(): Elements {
        return doc.select(".field-body p")
    }

    private fun thewashingtonpost() : Elements {
        val a = doc.select("article p").filter {
            !it.hasClass("interstitial-link ") &&
                !it.hasClass("trailer")
        }
        return Elements(a)
    }

    fun getBodyParagraphs(): Elements {
        try {
            println(hostName)
            return javaClass.getDeclaredMethod(hostName.replace(" ","")).invoke(this) as Elements
        } catch (e: Exception) {

            // NoSuchMethodException is normal, it means the host was unrecognized
            if (!(e is NoSuchMethodException))
                e.printStackTrace()

            /* if (e is NoSuchMethodException) */
            // Default behavior
            return doc.getElementsByTag("p")
        }
    }
}