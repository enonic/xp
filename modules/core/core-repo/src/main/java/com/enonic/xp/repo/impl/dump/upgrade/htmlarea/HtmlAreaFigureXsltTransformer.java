package com.enonic.xp.repo.impl.dump.upgrade.htmlarea;

import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class HtmlAreaFigureXsltTransformer
{

    private final Transformer transformer;

    public HtmlAreaFigureXsltTransformer()
    {
        final URL url = getClass().getResource( "/com/enonic/xp/repo/impl/dump/upgrade/htmlarea/figure.xsl" );

        try
        {
            transformer = TransformerFactory.newInstance().
                newTransformer( new StreamSource( url.openStream() ) );
            transformer.setOutputProperty( OutputKeys.OMIT_XML_DECLARATION, "yes" );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Failed to create HTML Area Figure XSLT Transformer", e );
        }
    }

    public String transform( final String source )
    {
        try
        {
            final StringReader reader = new StringReader( "<!DOCTYPE test [ <!ENTITY nbsp \"&#160;\">]><root>" + source + "</root>" );
            final StringWriter writer = new StringWriter();
            transformer.transform( new StreamSource( reader ), new StreamResult( writer ) );
            return writer.toString();
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Failed to transform HTML Area property '" + source + "'", e );
        }
    }
}
