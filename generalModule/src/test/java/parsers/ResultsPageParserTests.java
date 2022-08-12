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
}
