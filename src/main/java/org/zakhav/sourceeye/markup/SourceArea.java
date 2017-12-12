package org.zakhav.sourceeye.markup;

public class SourceArea
implements Markup
{
    private TextPoint start;
    private TextPoint end;
    private int leftEdgeColumn;
    private int rightEdgeColumn;

    public static SourceArea from(int row, int column)
    {
        SourceArea sourceArea = new SourceArea();
        sourceArea.start = new TextPoint(row, column);
        return sourceArea;
    }

    public SourceArea to(int row, int column)
    {
        this.end = new TextPoint(row, column);
        return this;
    }

    public TextPoint getStart()
    {
        return this.start;
    }

    public TextPoint getEnd()
    {
        return this.end;
    }

    public int getLeftEdgeColumn()
    {
        return this.leftEdgeColumn;
    }

    public int getRightEdgeColumn()
    {
        return this.rightEdgeColumn;
    }

    @Override
    public String toString()
    {
        return start + " : " + end;
    }

    @Override
    public void apply(MarkupVisitor visitor)
    {
        visitor.drawSourceArea(this);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        SourceArea that = (SourceArea) o;

        if (leftEdgeColumn != that.leftEdgeColumn) { return false; }
        if (rightEdgeColumn != that.rightEdgeColumn) { return false; }
        if (start != null ? !start.equals(that.start) : that.start != null) { return false; }
        return end != null ? end.equals(that.end) : that.end == null;
    }

    @Override
    public int hashCode()
    {
        int result = start != null ? start.hashCode() : 0;
        result = 31 * result + (end != null ? end.hashCode() : 0);
        result = 31 * result + leftEdgeColumn;
        result = 31 * result + rightEdgeColumn;
        return result;
    }

    public SourceArea edges(int newLeftEdgeColumn, int newRightEdgeColumn)
    {
        this.leftEdgeColumn = newLeftEdgeColumn;
        this.rightEdgeColumn = newRightEdgeColumn;

        return this;
    }

    public int rowCount()
    {
        return this.end.getRow() - this.start.getRow() + 1;
    }
}
