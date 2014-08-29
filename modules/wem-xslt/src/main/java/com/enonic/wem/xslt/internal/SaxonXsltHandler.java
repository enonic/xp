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
import com.google.common.io.Closeables;

import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.api.resource.ResourceProblemException;

final class SaxonXsltHandler
{
    private final TransformerFactory factory;

    private final XsltProcessorErrors errors;

    private Source xsltSource;

    private Source xmlSource;

    private Map<String, Object> parameters;

    private Transformer transformer;

    public SaxonXsltHandler( final TransformerFactory factory )
    {
        this.factory = factory;
        this.errors = new XsltProcessorErrors();
    }

    public void setXsltSource( final ResourceKey xslt )
    {
        final Resource resource = Resource.from( xslt );
        resource.requireExists();

        this.xsltSource = new StreamSource( resource.getUrl().toString() );
    }

    public void setXmlSource( final String inputXml )
    {
        this.xmlSource = new StreamSource( new StringReader( inputXml ) );
    }

    public void setParameters( final Map<String, Object> parameters )
    {
        this.parameters = parameters;
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

        return ResourceProblemException.newBuilder().
            lineNumber( locator.getLineNumber() ).
            resource( toResourceKey( locator.getSystemId() ) ).
            cause( e ).
            message( e.getMessage() ).
            build();
    }

    private ResourceKey toResourceKey( final String systemId )
    {
        try
        {
            return ResourceKey.from( new URL( systemId ) );
        }
        catch ( final IOException e )
        {
            throw Throwables.propagate( e );
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
