package nl.stokpop.jmh;

import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LetsRegexTest {

    private static final String TWO_OR_MORE_SLASHES_REGEXP = "//+";
    private static final Pattern SLASH_PATTERN = Pattern.compile(TWO_OR_MORE_SLASHES_REGEXP);

    @Test
    public void testSlashesToOne() {
        String text = "//sdl///kfjsdl/fka////sdl//kfjsadlkf//";
        String expected = "/sdl/kfjsdl/fka/sdl/kfjsadlkf/";
        assertEquals(expected, SLASH_PATTERN.matcher(text).replaceAll("/"));
        assertEquals(expected, LetsRegex.replaceMultipleCharsByOneChar(text, '/'));
    }

    @Test
    public void testLastSlash() {
        String testText = "/sdl/kfjsdl/fka/sdl/kfjsadlkf/";
        String expectedText = "/sdl/kfjsdl/fka/sdl/kfjsadlkf";
        assertEquals(expectedText, testText.substring(0, testText.length() - 1));
    }

    @Test
    public void testReplaceMultipleCharsByOneChar() {
        assertEquals(null, LetsRegex.replaceMultipleCharsByOneChar(null, '/'));
        assertEquals("", LetsRegex.replaceMultipleCharsByOneChar("", '/'));
        assertEquals("/", LetsRegex.replaceMultipleCharsByOneChar("/", '/'));
        assertEquals("/", LetsRegex.replaceMultipleCharsByOneChar("//", '/'));
        assertEquals("/\n", LetsRegex.replaceMultipleCharsByOneChar("///\n", '/'));
        assertEquals("a/", LetsRegex.replaceMultipleCharsByOneChar("a///", '/'));
        assertEquals("/a/", LetsRegex.replaceMultipleCharsByOneChar("///a///", '/'));
        assertEquals("/a/b/", LetsRegex.replaceMultipleCharsByOneChar("///a//b///", '/'));
        assertEquals(" foo bar ", LetsRegex.replaceMultipleCharsByOneChar("   foo    bar  ", ' '));
        assertEquals("foo\nbar", LetsRegex.replaceMultipleCharsByOneChar("foo\n\nbar", '\n'));
        assertEquals("foo☺bar☺", LetsRegex.replaceMultipleCharsByOneChar("foo☺☺bar☺☺☺☺", '\u263A'));
    }
}