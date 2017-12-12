package org.zakhav.sourceeye;

import org.eclipse.collections.api.list.ListIterable;
import org.zakhav.sourceeye.markup.Markup;

public class SourceEyeOverlayRunner
{
    public static void main(String[] args)
    {
        SourceEyeOverlayRunner runner = new SourceEyeOverlayRunner();
        runner.run();
    }

    private void run()
    {
        String source =
                "public class Fooinator\n" +
                "{\n" +
                "    public void foo(int a)\n" +
                "    {\n" +
                "        int b = 5; // variables are good\n" +
                "        \n" +
                "        // we check a condition right here\n" +
                "        // let's warn them\n" +
                "        System.out.println(\"we may print a\");" +
                "\n" +
                "        if (a > 5)\n" +
                "        {\n" +
                "            /* and then... whoa... */\n" +
                "            System.out.println(a);\n" +
                "        }\n" +
                "        \n" +
                "        if (b < 5)\n" +
                "        {\n" +
                "            /* this is insane! */\n" +
                "            System.out.println(b);\n" +
                "        }\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "public class Booinator {\n" +
                "    public void boo(int a) {\n" +
                "        int b = 5; // variables are good\n" +
                "        // we check a condition right here\n" +
                "        // let's warn them\n" +
                "        System.out.println(\"we may print a\");\n" +
                "        if (a > 5) {\n" +
                "            /* and then... whoa... */\n" +
                "            System.out.println(a);\n" +
                "        }\n" +
                "        if (b < 5) {\n" +
                "            /* this is insane! */\n" +
                "            System.out.println(b);\n" +
                "        }\n" +
                "    }\n" +
                "}";


        System.out.println(source);

        ListIterable<Markup> markup = (new SourceMarker(source)).getMarkup();

        ImageGenerator imageGenerator = new ImageGenerator();
        imageGenerator.createImage(source, markup);
        imageGenerator.saveImageToFile("C:\\Users\\Vovkin\\projects\\sourceeye\\marked_up_source");
    }
}
