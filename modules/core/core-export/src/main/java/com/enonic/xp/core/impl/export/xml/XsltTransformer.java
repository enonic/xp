package com.enonic.xp.core.impl.export.xml;

import java.io.Reader;
import java.io.StringWriter;
import java.net.URL;
import java.util.Map;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import com.google.common.io.CharSource;

import com.enonic.xp.export.ImportNodeException;


public final class XsltTransformer
{
    private final Transformer transformer;

    private XsltTransformer( final Transformer transformer )
    {
        this.transformer = transformer;
    }

    public String transform( final CharSource source )
        throws Exception
    {
        final StringWriter stringWriter = new StringWriter();

        try (Reader reader = source.openStream())
        {
            this.transformer.transform( new StreamSource( reader ), new StreamResult( stringWriter ) );
            return stringWriter.toString();
        }
    }

    public static XsltTransformer create( final URL script, final Map<String, Object> params )
    {
        final TransformerFactory tf = TransformerFactory.newInstance();

        try
        {
            final Transformer transformer = tf.newTransformer( new StreamSource( script.openStream() ) );
            transformer.setOutputProperty( OutputKeys.OMIT_XML_DECLARATION, "yes" );

            if ( params != null )
            {
                params.forEach( transformer::setParameter );
            }

            return new XsltTransformer( transformer );
        }
        catch ( Exception e )
        {
            throw new ImportNodeException( "Failed to create transformer from path '" + script + "'", e );
        }
    }

}
