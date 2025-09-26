package eu.kanade.tachiyomi.extension.id.soulscans

import eu.kanade.tachiyomi.source.model.*
import eu.kanade.tachiyomi.source.online.ParsedHttpSource
import okhttp3.Request
import org.jsoup.nodes.Element

class SoulScans : ParsedHttpSource() {

    override val name = "SoulScans"
    override val baseUrl = "https://soulscans.my.id"
    override val lang = "id"
    override val supportsLatest = true

    override fun popularMangaRequest(page: Int): Request =
        GET("$baseUrl/manga/page/$page", headers)

    override fun popularMangaSelector() = "div.bs div.bsx"

    override fun popularMangaFromElement(element: Element): SManga {
        val manga = SManga.create()
        manga.url = element.select("a").attr("href")
        manga.title = element.select("a div.bigor div.tt").text()
        manga.thumbnail_url = element.select("img").attr("src")
        return manga
    }

    override fun popularMangaNextPageSelector() = "a.next"

    override fun latestUpdatesRequest(page: Int) = popularMangaRequest(page)
    override fun latestUpdatesSelector() = popularMangaSelector()
    override fun latestUpdatesFromElement(element: Element) = popularMangaFromElement(element)
    override fun latestUpdatesNextPageSelector() = popularMangaNextPageSelector()

    override fun searchMangaRequest(page: Int, query: String, filters: FilterList): Request =
        GET("$baseUrl/page/$page?s=$query", headers)

    override fun searchMangaSelector() = popularMangaSelector()
    override fun searchMangaFromElement(element: Element) = popularMangaFromElement(element)
    override fun searchMangaNextPageSelector() = popularMangaNextPageSelector()

    override fun mangaDetailsParse(document: org.jsoup.nodes.Document): SManga {
        val manga = SManga.create()
        manga.description = document.select("div.entry-content p").text()
        manga.author = document.select("div.author a").text()
        return manga
    }

    override fun chapterListSelector() = "li.wp-manga-chapter a"
    override fun chapterFromElement(element: Element): SChapter {
        val chapter = SChapter.create()
        chapter.url = element.attr("href")
        chapter.name = element.text()
        return chapter
    }

    override fun pageListParse(document: org.jsoup.nodes.Document): List<Page> {
        return document.select("div.reading-content img").mapIndexed { i, img ->
            Page(i, "", img.attr("src"))
        }
    }

    override fun imageUrlParse(document: org.jsoup.nodes.Document): String =
        document.select("img").attr("src")
}
