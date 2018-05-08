import org.hamcrest.Matchers;
import org.junit.Test;
import parsingWebsite.ParsingMain;

import java.util.regex.Pattern;

import static org.junit.Assert.assertThat;


public class TestString {

    String testURL = "https://index.hu/24ora/?word=1&pepe=1&tol=1999-01-01&ig=2018-05-01&s=Orb%C3%A1n+Viktor";
    String testURL2 = "https://index.hu/24ora/?word=1&pepe=1&tol=1999-01-01&ig=2018-05-01&s=Microsoft";
    String testURL3 = "http://cimkezes.origo.hu/cimkek/microsoft/index.html?tag=Microsoft";


    Pattern pattern = Pattern.compile("https://index.hu/24ora/\\?word=1&pepe=1&tol=(\\d{4}-\\d{2}-\\d{2})&ig=(\\d{4}-\\d{2}-\\d{2})&s=(.*)");

    @Test
    public void testURL() {
        assertThat(testURL, Matchers.matchesPattern(pattern));
    }
}
