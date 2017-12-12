package org.zakhav.sourceeye;

import org.eclipse.collections.api.list.ListIterable;
import org.zakhav.sourceeye.markup.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class ImageGenerator
implements MarkupVisitor
{
    private BufferedImage canvas;
    private int lineHeight;

    private Font font = new Font("Consolas", Font.BOLD, 10);
    private FontMetrics fontMetrics;

    private Color textColor = Color.BLACK;
    private Color backgroundColor = Color.WHITE;

    private Graphics2D graphics;

    private String[] sourceLines;

    public void createCanvas(int width, int height)
    {
        this.canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        this.lineHeight = this.canvas.getHeight() / 40;

        this.graphics = this.canvas.createGraphics();

        graphics.setBackground ( this.backgroundColor );
        graphics.clearRect ( 0, 0, this.canvas.getWidth(), this.canvas.getHeight() );
    }


    public BufferedImage createImage(String source, ListIterable<Markup> markup)
    {
        this.createCanvas(1024, 1024);
        this.drawText(source);
        this.drawMarkup(markup);
        return this.canvas;
    }

    private void drawMarkup(ListIterable<Markup> markup)
    {
        markup.each(m -> m.apply(this));
    }

    private void drawText(String source)
    {
        this.sourceLines = source.split("\n");

        float fontHeight = (float) (0.8 * lineHeight);

        Font font = this.font.deriveFont(fontHeight);

        this.graphics.setColor(this.textColor);
        this.graphics.setFont(font);

        this.fontMetrics = this.graphics.getFontMetrics();

        for (int i = 0; i < this.sourceLines.length; i++)
        {
            this.graphics.drawString(String.format("%03d", i+1) + " " + this.sourceLines[i], 10, (i + 1) * this.lineHeight);
        }
    }

    @Override
    public void drawLine(Line line)
    {
        int quarter = this.lineHeight/4;

        TextPoint imageStart = this.sourceCoordToImageCoord(line.getStart(), fontMetrics, quarter);
        TextPoint imageEnd = this.sourceCoordToImageCoord(line.getEnd(), fontMetrics, quarter);

        AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
        this.graphics.setComposite(alphaComposite);

        this.graphics.setColor(Color.MAGENTA);
        this.graphics.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        this.graphics.drawLine(imageStart.getColumn(), imageStart.getRow(), imageEnd.getColumn(), imageEnd.getRow());
    }

    @Override
    public void drawSourceArea(SourceArea sourceArea)
    {
//        int rowOffset = this.lineHeight/8;
//        int colOffset = this.fontMetrics.stringWidth("a");

//        TextPoint imageStart = this.sourceCoordToImageCoord(sourceArea.getStart(), fontMetrics, 0);
//        TextPoint imageEnd = this.sourceCoordToImageCoord(sourceArea.getEnd(), fontMetrics, 0);
//
        AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
        this.graphics.setComposite(alphaComposite);

        this.graphics.setColor(Color.GREEN);
        this.graphics.setStroke(new BasicStroke(1));

        /*
         up to 3 rectangles:
                 TTTTT
              MMMMMMMM
              BBBBB
          */
        // top
        TextPoint rect1Start = sourceArea.getStart();
        TextPoint rect1End = new TextPoint(rect1Start.getRow(), sourceArea.getRightEdgeColumn());
        this.fillRect(rect1Start, rect1End);

        if (sourceArea.rowCount() > 2)
        {
            // middle
            TextPoint rect2Start = new TextPoint(sourceArea.getStart().getRow() + 1, sourceArea.getLeftEdgeColumn());
            TextPoint rect2End = new TextPoint(sourceArea.getEnd().getRow()-1, sourceArea.getRightEdgeColumn());
            this.fillRect(rect2Start, rect2End);
        }

        if (sourceArea.rowCount() > 1)
        {
            // bottom
            TextPoint rect3Start = new TextPoint(sourceArea.getEnd().getRow(), sourceArea.getLeftEdgeColumn());
            TextPoint rect3End = sourceArea.getEnd();
            this.fillRect(rect3Start, rect3End);
        }

//        this.graphics.fillRect(
//                imageStart.getColumn(), imageStart.getRow() - this.lineHeight + rowOffset,
//                imageEnd.getColumn() + colOffset- imageStart.getColumn(), imageEnd.getRow() + this.lineHeight + rowOffset - imageStart.getRow());
    }

    private void fillRect(TextPoint start, TextPoint end)
    {
        TextPoint imageStart = this.sourceCoordToImageCoord(start, this.fontMetrics, 0);
        TextPoint imageEnd = this.sourceCoordToImageCoord(end, this.fontMetrics, 0);

        int rowOffset = this.lineHeight/8;
        int colOffset = this.fontMetrics.stringWidth("a");

        this.graphics.fillRect(
                imageStart.getColumn(), imageStart.getRow() - this.lineHeight + rowOffset,
                imageEnd.getColumn() + colOffset- imageStart.getColumn(), imageEnd.getRow() + this.lineHeight + rowOffset - imageStart.getRow());
    }

    private TextPoint sourceCoordToImageCoord(TextPoint sourceCoordinate, FontMetrics fontMetrics, int offset)
    {
        int sourceRow = sourceCoordinate.getRow();
        int sourceColumn = sourceCoordinate.getColumn();

        int imageRow = sourceRow*lineHeight;

//        int imageColumn = fontMetrics.stringWidth("000 " + this.sourceLines[sourceRow-1].substring(0, sourceColumn));
        int imageColumn = fontMetrics.stringWidth("000 " + this.stringOfSize(sourceColumn));

        return new TextPoint(imageRow - offset, imageColumn + offset);
    }

    private String stringOfSize(int size)
    {
        char[] chars = new char[size];
        Arrays.fill(chars, 'A');
        return new String(chars);
    }

    public File saveImageToFile(String fileName)
    {
        String format = "JPG";
        try
        {
            File output = new File(fileName+"."+format.toLowerCase());
            boolean cache = ImageIO.getUseCache();
            ImageIO.setUseCache(false);
            ImageIO.write(this.canvas, format, output);
            ImageIO.setUseCache(cache);
            return output;
        }
        catch (IOException e)
        {
            throw new RuntimeException("Failed to write to file'" + fileName + "' because of \"" + e.getMessage() + '"', e);
        }
    }

}
