package com.enonic.xp.lib.xslt;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import com.google.common.io.Closeables;

import com.enonic.xp.portal.script.ScriptValue;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceProblemException;

public final class XsltProcessor
{
    private final TransformerFactory factory;

    private final XsltProcessorErrors errors;

    private Source xsltSource;

    private Source xmlSource;

    private Transformer transformer;

    public XsltProcessor( final TransformerFactory factory )
    {
        this.factory = factory;
        this.errors = new XsltProcessorErrors();
    }

    public void setView( final ResourceKey view )
    {
        final Resource resource = Resource.from( view );
        resource.requireExists();

        this.xsltSource = new StreamSource( resource.getUrl().toString() );
    }

    public void setModel( final ScriptValue model )
    {
        if ( model != null )
        {
            this.xmlSource = MapToXmlConverter.toSource( model.getMap() );
        }
        else
        {
            this.xmlSource = MapToXmlConverter.toSource( Maps.newHashMap() );
        }
    }

    public String process()
    {
        try
        {
            return doProcess();
        }
        catch ( final Exception e )
        {
            if ( this.errors.hasErrors() )
            {
                throw handleError( this.errors.iterator().next() );
            }

            throw handleError( e );
        }
    }

    private RuntimeException handleError( final Exception e )
    {
        if ( e instanceof TransformerException )
        {
            return handleError( (TransformerException) e );
        }

        if ( e instanceof RuntimeException )
        {
            return (RuntimeException) e;
        }

        return Throwables.propagate( e );
    }

    private RuntimeException handleError( final TransformerException e )
    {
        final SourceLocator locator = e.getLocator();
        final String systemId = locator != null ? locator.getSystemId() : null;

        if ( systemId != null )
        {
            return ResourceProblemException.newBuilder().
                lineNumber( locator.getLineNumber() ).
                resource( toResourceKey( systemId ) ).
                cause( e ).
                message( e.getMessage() ).
                build();
        }

        return Throwables.propagate( e );
    }

    private ResourceKey toResourceKey( final String systemId )
    {
        try
        {
            return ResourceKey.from( new URL( systemId ) );
        }
        catch ( final IOException e )
        {
            return null;
        }
    }

    private String doProcess()
        throws Exception
    {
        createTransformer();

        final StringWriter out = new StringWriter();
        final StreamResult result = new StreamResult( out );

        try
        {
            this.transformer.transform( this.xmlSource, result );
            return out.getBuffer().toString();
        }
        finally
        {
            Closeables.close( out, false );
        }
    }

    protected void createTransformer()
        throws Exception
    {
        this.factory.setErrorListener( this.errors );
        this.factory.setURIResolver( new UriResolverImpl() );
        this.transformer = this.factory.newTransformer( this.xsltSource );
        this.transformer.setErrorListener( this.errors );
        this.transformer.setOutputProperty( OutputKeys.OMIT_XML_DECLARATION, "yes" );
    }
}
