package org.zakhav.sourceeye.markup;

public interface MarkupVisitor
{
    default void drawLine(Line line)
    {
        throw new RuntimeException("Don't know how to apply line");
    };

    default void drawSourceArea(SourceArea sourceArea)
    {
        throw new RuntimeException("Don't know how to apply source area");
    };

}
