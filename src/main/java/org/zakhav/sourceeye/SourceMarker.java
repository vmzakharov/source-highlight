package org.zakhav.sourceeye;

import org.eclipse.collections.api.list.ListIterable;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.stack.MutableStack;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Stacks;
import org.zakhav.sourceeye.markup.Line;
import org.zakhav.sourceeye.markup.Markup;
import org.zakhav.sourceeye.markup.SourceArea;

import static org.zakhav.sourceeye.SourceMarker.ParseState.*;

public class SourceMarker
{
    private String source;

    private int currentRow = 1;
    private int currentColumn = 0;

    private int currentStateRow = 1;
    private int currentStateColumn = 1;

    private int rightEdgeColumn;
    private int leftEdgeColumn;

    private ParseState currentState = UNDEFINED;

    private int charIndex = 0;
    private char lookAhead = 0;

    public SourceMarker(String newSource)
    {
        this.source = newSource;
    }

    public ListIterable<Markup> getMarkup()
    {
        MutableList<Markup> markup = Lists.mutable.of();

        MutableStack<Line> lines = Stacks.mutable.of();
        MutableStack<SourceArea> sourceAreas = Stacks.mutable.of();

        ParseState state;
        while ((state = this.getNextState()) != END_SOURCE)
        {
            switch (state)
            {
                case OPEN_BRACE:
                    lines.push(Line.from(this.currentStateRow, this.currentStateColumn));
                    break;
                case CLOSE_BRACE:
                    Line line = lines.pop();
                    line.to(this.currentStateRow, this.currentStateColumn);
                    markup.add(line);
                    break;
                case START_BLOCK:
                    sourceAreas.push(SourceArea.from(this.currentStateRow, this.currentStateColumn));
                    break;
                case END_BLOCK:
                    SourceArea sourceArea = sourceAreas.pop();
                    sourceArea.to(this.currentStateRow, this.currentStateColumn).edges(this.leftEdgeColumn, this.rightEdgeColumn);
                    markup.add(sourceArea);
                    break;
            }
        }

        return markup;
    }

    private ParseState getNextState()
    {

        if (this.charIndex >= this.source.length())
        {
            return END_SOURCE;
        }

        char c;

        do
        {
            c = getNextChar();
        }
        while (Character.isWhitespace(c));

        this.currentStateRow = this.currentRow;
        this.currentStateColumn = this.currentColumn;

        if (c == '{')
        {
            this.currentState = OPEN_BRACE;
        }
        else if (c == '}')
        {
            this.currentState = CLOSE_BRACE;
        }
        else
        {
            if (this.currentState == START_BLOCK)
            {
                this.processBlock(c);
                this.currentState = END_BLOCK;
            }
            else
            {
                this.currentState = START_BLOCK;
            }
        }

        return this.currentState;
    }

    private void processBlock(char c)
    {
        this.leftEdgeColumn = this.currentColumn;
        this.rightEdgeColumn = 0;

        boolean inIgnorableArea = false; // in a comment or in a string literal or in a char literal
        boolean blankLine = true; // the entire line just read is white space

        // we are in the block so continue until a brace or something else not a block
        // or an empty line
        while (this.doesNotLookLikeAnythingToMe(c))
        {
            c = getNextChar();
            if (!Character.isWhitespace(c))
            {
                blankLine = false;
                if (this.doesNotLookLikeAnythingToMe(c))
                {
                    this.currentStateColumn = this.currentColumn;
                    this.currentStateRow = this.currentRow;

                    if (this.currentColumn < this.leftEdgeColumn)
                    {
                        this.leftEdgeColumn = this.currentColumn;
                    }

                    if (this.currentColumn > this.rightEdgeColumn)
                    {
                        this.rightEdgeColumn = this.currentColumn;
                    }
                }
            }

            // empty line - end of block
            if (c == '\n')
            {
                if (blankLine)
                {
                    break;
                }

                blankLine = true;
            }
        }

        this.ungetChar(c);
    }

    private void ungetChar(char c)
    {
        this.lookAhead = c;
    }

    private boolean doesNotLookLikeAnythingToMe(char c)
    {
        return c != 0 && c != '{' && c != '}';
    }

    private char getNextChar()
    {
        char c;
        if (this.lookAhead != 0)
        {
            c = this.lookAhead;
            this.lookAhead = 0;
        }
        else
        {
            c = source.charAt(this.charIndex);
            if (c == '\n')
            {
                this.currentRow++;
                this.currentColumn = 0;
            }
            else
            {
                this.currentColumn++;
            }

            this.charIndex++;
        }

        return c;
    }

    public enum ParseState
    {
        OPEN_BRACE, CLOSE_BRACE, START_BLOCK, END_BLOCK, END_SOURCE, UNDEFINED
    }
}
