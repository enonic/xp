package com.enonic.wem.xml.schema;

import java.net.URL;
import java.util.concurrent.Callable;

import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import com.enonic.wem.xml.XmlException;

public final class XmlSchemas
{
    private final static XmlSchemas INSTANCE = new XmlSchemas();

    private final Cache<String, Schema> cache;

    private final SchemaFactory schemaFactory;

    private XmlSchemas()
    {
        this.cache = CacheBuilder.newBuilder().maximumSize( 10 ).build();
        this.schemaFactory = SchemaFactory.newInstance( XMLConstants.W3C_XML_SCHEMA_NS_URI );
    }

    public static Schema getSchema( final String name )
    {
        return INSTANCE.getOrLoadSchema( name );
    }

    private Schema getOrLoadSchema( final String name )
    {
        try
        {
            return this.cache.get( name, new Callable<Schema>()
            {
                @Override
                public Schema call()
                    throws Exception
                {
                    return loadSchema( name );
                }
            } );
        }
        catch ( final Exception e )
        {
            final Throwable cause = e.getCause();
            if ( cause instanceof XmlException )
            {
                throw (XmlException) cause;
            }

            throw new XmlException( "Failed to load [" + name + "] schema", cause );
        }
    }

    private Schema loadSchema( final String name )
        throws Exception
    {
        final URL url = getClass().getResource( name + ".xsd" );
        if ( url == null )
        {
            throw new XmlException( "Could not find [" + name + "] schema" );
        }

        return this.schemaFactory.newSchema( url );
    }
}
