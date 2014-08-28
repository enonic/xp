package com.enonic.wem.xslt.internal;

import java.io.StringWriter;
import java.util.Map;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

import com.google.common.io.Closeables;

import com.enonic.wem.xslt.XsltProcessorException;
import com.enonic.wem.xslt.XsltProcessorParams;

final class SaxonXsltHandler
{
    private final TransformerFactory factory;

    private final XsltProcessorErrors errors;

    private final XsltProcessorParams spec;

    private Transformer transformer;

    public SaxonXsltHandler( final TransformerFactory factory, final XsltProcessorParams spec )
    {
        this.factory = factory;
        this.errors = new XsltProcessorErrors();
        this.spec = spec;
    }

    public String process()
        throws XsltProcessorException
    {
        try
        {
            return doProcess();
        }
        catch ( final Exception e )
        {
            throw new XsltProcessorException( e, this.errors );
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
            this.transformer.transform( this.spec.getSource(), result );
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
        this.transformer = this.factory.newTransformer( this.spec.getXsl() );
        this.transformer.setErrorListener( this.errors );
    }

    private void setParameters()
    {
        for ( final Map.Entry<String, Object> entry : this.spec.getParameters().entrySet() )
        {
            this.transformer.setParameter( entry.getKey(), entry.getValue() );
        }
    }
}
