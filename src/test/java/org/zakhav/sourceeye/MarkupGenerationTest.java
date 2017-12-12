package org.zakhav.sourceeye;

import org.eclipse.collections.api.list.ListIterable;
import org.eclipse.collections.impl.test.Verify;
import org.junit.Assert;
import org.junit.Test;
import org.zakhav.sourceeye.markup.Line;
import org.zakhav.sourceeye.markup.Markup;
import org.zakhav.sourceeye.markup.SourceArea;

public class MarkupGenerationTest
{
    @Test
    public void noStringsNoComments()
    {
        String s = "public void foo(int a)\n" +
                "{\n" +
                "    if (3 > 5)\n" +
                "    {\n" +
                "        System.out.println(a);\n" +
                "    }\n" +
                "}";

        SourceMarker sourceMarker = new SourceMarker(s);

        ListIterable<Markup> markup = sourceMarker.getMarkup();

        Verify.assertSize(5, markup);

        ListIterable<Line> lines = markup.selectInstancesOf(Line.class);
        ListIterable<SourceArea> sourceAreas = markup.selectInstancesOf(SourceArea.class);

        Verify.assertSize(2, lines);
        Verify.assertSize(3, sourceAreas);

        Verify.assertContainsAll(lines, Line.from(4, 5).to(6, 5), Line.from(2, 1).to(7, 1));
        Verify.assertContainsAll(sourceAreas,
                SourceArea.from(1, 1).to(1, 22),
                SourceArea.from(3, 5).to(3, 14),
                SourceArea.from(5, 9).to(5, 30));

        System.out.println(s);
        System.out.println(markup.makeString("\n"));
    }

    @Test
    public void withComments()
    {
        String s = "public void foo(int a)\n" +
                "{\n" +
                "    int b = 5; // variables are good\n" +
                "\n" +
                "    // we check a condition right here\n" +
                "    if (a > 5)\n" +
                "    {\n" +
                "        /* and then... whoa... */\n" +
                "        System.out.println(a);\n" +
                "    }\n" +
                "}";

        SourceMarker sourceMarker = new SourceMarker(s);

        ListIterable<Markup> markup = sourceMarker.getMarkup();

        System.out.println(s);
        System.out.println(markup.makeString("\n"));

        Verify.assertSize(6, markup);

        ListIterable<Line> lines = markup.selectInstancesOf(Line.class);
        ListIterable<SourceArea> sourceAreas = markup.selectInstancesOf(SourceArea.class);

        Verify.assertSize(2, lines);
        Verify.assertSize(4, sourceAreas);

        Verify.assertContainsAll(lines,
                Line.from(7, 5).to(10, 5),
                Line.from(2, 1).to(11, 1));
        Verify.assertContainsAll(sourceAreas,
                SourceArea.from(1, 1).to(1, 22),
                SourceArea.from(3, 5).to(3, 14),
                SourceArea.from(5, 5).to(6, 14),
                SourceArea.from(8, 9).to(9, 30));
    }

    @Test
    public void noStringsNoCommentsAltLayout()
    {
        String s =
                "public void foo(int a) {\n" +
                "    if (3 > 5) {\n" +
                "        System.out.println(a);\n" +
                "    }\n" +
                "}";

        SourceMarker sourceMarker = new SourceMarker(s);

        ListIterable<Markup> markup = sourceMarker.getMarkup();

        Verify.assertSize(5, markup);

        ListIterable<Line> lines = markup.selectInstancesOf(Line.class);
        ListIterable<SourceArea> sourceAreas = markup.selectInstancesOf(SourceArea.class);

        Verify.assertSize(2, lines);
        Verify.assertSize(3, sourceAreas);

        Verify.assertContainsAll(lines,
                Line.from(2, 16).to(4, 5),
                Line.from(1, 24).to(5, 1));
        Verify.assertContainsAll(sourceAreas,
                SourceArea.from(1, 1).to(1, 22),
                SourceArea.from(2, 5).to(2, 14),
                SourceArea.from(3, 9).to(3, 30));

        System.out.println(s);
        System.out.println(markup.makeString("\n"));
    }


}
