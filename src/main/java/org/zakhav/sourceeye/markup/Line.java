package org.zakhav.sourceeye.markup;

public class Line
implements Markup
{
    private TextPoint start;
    private TextPoint end;

    public static Line from(int row, int column)
    {
        Line l = new Line();
        l.start = new TextPoint(row, column);
        return l;
    }

    public Line to(int row, int column)
    {
        this.end = new TextPoint(row, column);
        return this;
    }

    @Override
    public String toString()
    {
        return start + " - " + end;
    }

    @Override
    public void apply(MarkupVisitor visitor)
    {
        visitor.drawLine(this);
    }

    public TextPoint getStart()
    {
        return this.start;
    }

    public TextPoint getEnd()
    {
        return this.end;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        Line line = (Line) o;

        if (start != null ? !start.equals(line.start) : line.start != null) { return false; }
        return end != null ? end.equals(line.end) : line.end == null;
    }

    @Override
    public int hashCode()
    {
        int result = start != null ? start.hashCode() : 0;
        result = 31 * result + (end != null ? end.hashCode() : 0);
        return result;
    }
}
