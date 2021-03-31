package nl.stokpop.jmh;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LetsRegex2Test {

    @Test
    void sanitize() {
        //compareSanitizeRegexpAndUtil(null);
        compareSanitizeRegexpAndUtil("");
        compareSanitizeRegexpAndUtil("foo bar");
        compareSanitizeRegexpAndUtil("foo  bar");
        compareSanitizeRegexpAndUtil(" foo bar ");
        compareSanitizeRegexpAndUtil("\n\nfoo\r\r\r bar\f");
        compareSanitizeRegexpAndUtil(" -foo- -bar - ");
    }

    @Test
    void sanitizeOriginalTests() {
        assertEquals(LetsRegex2.sanitize("Foo Bar"),"Foo-Bar");
        assertEquals(LetsRegex2.sanitize(" Foo Bar "),"Foo-Bar");
        assertEquals(LetsRegex2.sanitize(" Foo Bar"),"Foo-Bar");
        assertEquals(LetsRegex2.sanitize("Foo Bar "),"Foo-Bar");
        assertEquals(LetsRegex2.sanitize("  Foo Bar  "),"Foo-Bar");
        assertEquals(LetsRegex2.sanitize("Foo@Bar"),"Foo@Bar");
        assertEquals(LetsRegex2.sanitize("Foó Bar"),"Foó-Bar");
        assertEquals(LetsRegex2.sanitize("||ó/."),"||ó/.");
        assertEquals(LetsRegex2.sanitize("${Foo:Bar:baz}"),"${Foo:Bar:baz}");
        assertEquals(LetsRegex2.sanitize("St. Foo's of Bar"),"St.-Foo's-of-Bar");
        assertEquals(LetsRegex2.sanitize("(Foo and (Bar and (Baz)))"),"(Foo-and-(Bar-and-(Baz)))");
        assertEquals(LetsRegex2.sanitize("Foo.bar.baz"),"Foo.bar.baz");
        assertEquals(LetsRegex2.sanitize("FooBar"),"FooBar");
    }

    @Test
    void sanitizeOriginalTestsNewImplementation() {
        assertEquals(LetsRegex2.replaceWhitespaceWithChar("Foo Bar", '-'),"Foo-Bar");
        assertEquals(LetsRegex2.replaceWhitespaceWithChar(" Foo Bar ", '-'),"Foo-Bar");
        assertEquals(LetsRegex2.replaceWhitespaceWithChar(" Foo Bar", '-'),"Foo-Bar");
        assertEquals(LetsRegex2.replaceWhitespaceWithChar("Foo Bar ", '-'),"Foo-Bar");
        assertEquals(LetsRegex2.replaceWhitespaceWithChar("  Foo Bar  ", '-'),"Foo-Bar");
        assertEquals(LetsRegex2.replaceWhitespaceWithChar("Foo@Bar", '-'),"Foo@Bar");
        assertEquals(LetsRegex2.replaceWhitespaceWithChar("Foó Bar", '-'),"Foó-Bar");
        assertEquals(LetsRegex2.replaceWhitespaceWithChar("||ó/.", '-'),"||ó/.");
        assertEquals(LetsRegex2.replaceWhitespaceWithChar("${Foo:Bar:baz}", '-'),"${Foo:Bar:baz}");
        assertEquals(LetsRegex2.replaceWhitespaceWithChar("St. Foo's of Bar", '-'),"St.-Foo's-of-Bar");
        assertEquals(LetsRegex2.replaceWhitespaceWithChar("(Foo and (Bar and (Baz)))", '-'),"(Foo-and-(Bar-and-(Baz)))");
        assertEquals(LetsRegex2.replaceWhitespaceWithChar("Foo.bar.baz", '-'),"Foo.bar.baz");
        assertEquals(LetsRegex2.replaceWhitespaceWithChar("FooBar", '-'),"FooBar");
    }
    
    private void compareSanitizeRegexpAndUtil(String fooBar) {
        assertEquals(LetsRegex2.sanitize(fooBar), LetsRegex2.replaceWhitespaceWithChar(fooBar, '-'));
    }
}