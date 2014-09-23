package com.enonic.wem.xslt.internal;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.Map;

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

import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.api.resource.ResourceProblemException;
import com.enonic.wem.xslt.XsltProcessor;

final class XsltProcessorImpl
    implements XsltProcessor
{
    private final TransformerFactory factory;

    private final XsltProcessorErrors errors;

    private ResourceKey view;

    private Source xsltSource;

    private Source xmlSource;

    private final Map<String, Object> parameters;

    private Transformer transformer;

    public XsltProcessorImpl( final TransformerFactory factory )
    {
        this.factory = factory;
        this.errors = new XsltProcessorErrors();
        this.parameters = Maps.newHashMap();
    }

    @Override
    public XsltProcessor view( final ResourceKey view )
    {
        this.view = view;

        final Resource resource = Resource.from( view );
        resource.requireExists();

        this.xsltSource = new StreamSource( resource.getUrl().toString() );
        return this;
    }

    @Override
    public XsltProcessor inputXml( final String inputXml )
    {
        this.xmlSource = new StreamSource( new StringReader( inputXml ) );
        return this;
    }

    @Override
    public XsltProcessor parameters( final Map<String, Object> parameters )
    {
        this.parameters.putAll( parameters );
        return this;
    }

    @Override
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
        setParameters();

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
        this.transformer = this.factory.newTransformer( this.xsltSource );
        this.transformer.setErrorListener( this.errors );
        this.transformer.setOutputProperty( OutputKeys.OMIT_XML_DECLARATION, "yes" );
    }

    private void setParameters()
        throws Exception
    {
        final ContextDocBuilder contextDocBuilder = new ContextDocBuilder();
        this.transformer.setParameter( "_", contextDocBuilder.createContextDoc() );

        if ( this.parameters == null )
        {
            return;
        }

        for ( final Map.Entry<String, Object> entry : this.parameters.entrySet() )
        {
            this.transformer.setParameter( entry.getKey(), entry.getValue() );
        }
    }
}
