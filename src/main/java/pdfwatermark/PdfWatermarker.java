package pdfwatermark;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

/**
 * Utility class for adding a watermark text to the footer of every page of a
 * PDF document, using Apache PDFBox.
 *
 * @author Prof. Dr. David Buzatto
 */
public class PdfWatermarker {

    private static final float FONT_SIZE = 10;
    private static final Color TEXT_COLOR = Color.LIGHT_GRAY;

    private PdfWatermarker() {
    }

    /**
     * Reads inputFile, adds watermarkText to the footer of every page and
     * saves the result to outputFile.
     *
     * @param inputFile the PDF file to read
     * @param outputFile the PDF file to write the watermarked result to
     * @param watermarkText the text to place in the footer of each page
     * @throws IOException if the file cannot be read, processed or saved
     */
    public static void addWatermark( File inputFile, File outputFile, String watermarkText ) throws IOException {

        try ( PDDocument document = Loader.loadPDF( inputFile ) ) {

            PDFont font = new PDType1Font( Standard14Fonts.FontName.HELVETICA );

            for ( PDPage page : document.getPages() ) {

                PDRectangle pageSize = page.getMediaBox();
                float textWidth = font.getStringWidth( watermarkText ) / 1000 * FONT_SIZE;
                float x = ( pageSize.getWidth() - textWidth ) / 2;
                float y = 20; // bottom margin, distance from the footer

                // APPEND ensures the original page content is preserved
                // and the text is drawn on top of it
                try ( PDPageContentStream contentStream = new PDPageContentStream(
                        document, page, PDPageContentStream.AppendMode.APPEND, true, true ) ) {
                    contentStream.beginText();
                    contentStream.setFont( font, FONT_SIZE );
                    contentStream.setNonStrokingColor( TEXT_COLOR );
                    contentStream.newLineAtOffset( x, y );
                    contentStream.showText( watermarkText );
                    contentStream.endText();
                }
                
            }

            document.save( outputFile );
            
        }
        
    }

}
