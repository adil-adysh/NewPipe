package org.schabi.newpipe.transcript;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TranscriptParserTest {
    @Test
    public void convertsSrtToText() {
        assertEquals("Hello & welcome\nNext line", TranscriptParser.parse("srt",
                "1\n00:00:01,000 --> 00:00:02,000\n<i>Hello &amp; welcome</i>\n\n"
                + "2\n00:00:03,000 --> 00:00:04,000\nNext line\n"));
    }

    @Test
    public void convertsWebVttAndAvoidsRepeatedCues() {
        assertEquals("Hello world\nAgain", TranscriptParser.parse("vtt",
                "WEBVTT\n\n00:00:01.000 --> 00:00:02.000\n<c>Hello world</c>\n\n"
                + "00:00:02.000 --> 00:00:03.000\nHello world\n\n"
                + "00:00:04.000 --> 00:00:05.000\nAgain\n", true));
    }

    @Test
    public void convertsTtmlParagraphsAndLineBreaks() {
        assertEquals("Hello & welcome\nNext line", TranscriptParser.parse("ttml",
                "<tt><body><div><p begin=\"1s\">Hello &amp; <span>welcome</span>"
                + "<br/>Next line</p></div></body></tt>"));
    }
    @Test
    public void mergesRollingAutomaticCaptions() {
        assertEquals("Hello world again", TranscriptParser.parse("vtt",
                "WEBVTT\n\n00:00:01.000 --> 00:00:02.000\nHello\n\n"
                + "00:00:02.000 --> 00:00:03.000\nHello world\n\n"
                + "00:00:03.000 --> 00:00:04.000\nworld again\n", true));
    }
    @Test
    public void preservesRepeatedManualCaptions() {
        assertEquals("Yes\nYes", TranscriptParser.parse("srt",
                "1\n00:00:01,000 --> 00:00:02,000\nYes\n\n"
                + "2\n00:00:03,000 --> 00:00:04,000\nYes\n"));
    }

    @Test
    public void removesInlineVttTimestamps() {
        assertEquals("Hello world", TranscriptParser.parse("vtt",
                "WEBVTT\n\n00:00:01.000 --> 00:00:04.000\n"
                + "Hello <00:00:02.000>world\n"));
    }
}
