package org.zakhav.sourceeye.markup;

public class TextPoint
{
    private int row;
    private int column;

    public TextPoint(int newRow, int newColumn)
    {
        this.row = newRow;
        this.column = newColumn;
    }

    public int getRow()
    {
        return this.row;
    }

    public int getColumn()
    {
        return this.column;
    }

    @Override
    public String toString()
    {
        return "(" + this.row + ", " + this.column + "}";
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        TextPoint textPoint = (TextPoint) o;

        if (row != textPoint.row) { return false; }
        return column == textPoint.column;
    }

    @Override
    public int hashCode()
    {
        int result = row;
        result = 31 * result + column;
        return result;
    }
}
