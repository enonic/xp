package com.enonic.wem.xslt.internal;

import javax.xml.transform.TransformerFactory;

import net.sf.saxon.Configuration;
import net.sf.saxon.TransformerFactoryImpl;

import com.enonic.wem.xslt.XsltProcessor;
import com.enonic.wem.xslt.XsltRenderParams;

public final class SaxonXsltProcessor
    implements XsltProcessor
{
    private final Configuration configuration;

    public SaxonXsltProcessor()
    {
        this.configuration = new Configuration();
        this.configuration.setLineNumbering( true );
        this.configuration.setHostLanguage( Configuration.XSLT );
        this.configuration.setVersionWarning( false );
        this.configuration.setCompileWithTracing( true );
        this.configuration.setValidationWarnings( true );
    }

    private TransformerFactory createTransformerFactory()
    {
        return new TransformerFactoryImpl( this.configuration );
    }

    @Override
    public String render( final XsltRenderParams params )
    {
        final TransformerFactory factory = createTransformerFactory();
        final SaxonXsltHandler handler = new SaxonXsltHandler( factory );
        handler.setXsltSource( params.getView() );
        handler.setXmlSource( params.getInputXml() );
        handler.setParameters( params.getParameters() );
        return handler.process();
    }
}
