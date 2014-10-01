package com.enonic.wem.xslt.internal;

import javax.xml.transform.TransformerFactory;

import net.sf.saxon.Configuration;
import net.sf.saxon.TransformerFactoryImpl;

import com.enonic.wem.xslt.XsltProcessor;
import com.enonic.wem.xslt.XsltProcessorFactory;

public final class XsltProcessorFactoryImpl
    implements XsltProcessorFactory
{
    private final Configuration configuration;

    public XsltProcessorFactoryImpl()
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
    public XsltProcessor newProcessor()
    {
        final TransformerFactory factory = createTransformerFactory();
        return new XsltProcessorImpl( factory );
    }
}
