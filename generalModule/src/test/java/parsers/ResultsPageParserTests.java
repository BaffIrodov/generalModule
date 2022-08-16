package parsers;

import com.gen.GeneralModule.parsers.ResultPageParser;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

public class ResultsPageParserTests {

    @MockBean
    private ResultPageParser resultPageParser = new ResultPageParser();

    // Проверяем то, что нет пустых ссылок их ровно 100 штук, документ не пустой и в нём есть элемент
    // со ссылками на карты.
    @Test
    public void resultLinks(List<String> links, Document doc) {
        Assertions.assertFalse(links.contains(""));
        Assertions.assertFalse(links.contains(null));
        Assertions.assertNotEquals(0, links.size());
        Assertions.assertEquals(100, links.size());
        Assertions.assertNotEquals(null, doc);
        Elements elementsWithHrefs = doc.body().getElementsByClass("result-con");
        Assertions.assertNotEquals(null, elementsWithHrefs);
    }

    // Проверка страницы с матчем. Если нет кнопки "stats" под картой, то должно быть 0 ссылок на статсы.
    // Если их не ноль, то проверяем, что всего карт на странице должно быть 1, 3 или 5 и что ссылки
    // ссылки на статсы не должны быть пустыми.
    @Test
    public void statsLinks(Document doc) {
        List<String> statsLinks = resultPageParser.getAllStatsLinks(doc);
        Elements map = doc.body().getElementsByClass("results-stats");
        if (map.size() == 0) {
            Assertions.assertEquals(0, statsLinks.size());
        } else {
            map = doc.body().getElementsByClass("mapholder");
            Assertions.assertTrue(map.size() == 1 || map.size() == 3 || map.size() == 5);
            Assertions.assertFalse(statsLinks.contains(""));
            Assertions.assertFalse(statsLinks.contains(null));
            Assertions.assertNotEquals(0, statsLinks.size());
        }
    }
}
