package com.enonic.xp.repo.impl.dump.upgrade.htmlarea;

import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.Configuration;
import net.sf.saxon.TransformerFactoryImpl;

public class HtmlAreaFigureXsltTransformer
{

    private final Transformer transformer;

    public HtmlAreaFigureXsltTransformer()
    {
        final URL url = getClass().getResource( "/com/enonic/xp/repo/impl/dump/upgrade/htmlarea/figure.xsl" );

        try
        {
            transformer = createTransformerFactory().
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
//            final String source2 = source.replace( "&nbsp;", "<xsl:text disable-output-escaping=\"yes\">&nbsp;</xsl:text>" );
            final StringReader reader = new StringReader( "<!DOCTYPE test [ <!ENTITY nbsp \"&#160;\">]><root>" + source + "</root>" );
            final StringWriter writer = new StringWriter();
            transformer.transform( new StreamSource( reader ), new StreamResult( writer ) );
            final String target = writer.toString();
            return target.replaceAll( "\u00A0", "&nbsp;" );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Failed to transform HTML Area property '" + source + "'", e );
        }
    }

    private TransformerFactory createTransformerFactory()
    {
        return new TransformerFactoryImpl( createConfiguration() );
    }

    private Configuration createConfiguration()
    {
        final Configuration configuration = new Configuration();
        configuration.setLineNumbering( true );
        configuration.setHostLanguage( Configuration.XSLT );
        configuration.setVersionWarning( false );
        configuration.setCompileWithTracing( true );
        configuration.setValidationWarnings( true );
        return configuration;
    }
}
