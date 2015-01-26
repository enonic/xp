package com.enonic.xp.portal.xslt.impl;

import javax.xml.transform.TransformerFactory;

import net.sf.saxon.Configuration;
import net.sf.saxon.TransformerFactoryImpl;

import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.portal.xslt.impl.function.XsltFunctionLibrary;

final class XsltProcessorFactory
{
    protected PortalUrlService urlService;

    private Configuration configuration;

    public void initialize()
    {
        this.configuration = new Configuration();
        this.configuration.setLineNumbering( true );
        this.configuration.setHostLanguage( Configuration.XSLT );
        this.configuration.setVersionWarning( false );
        this.configuration.setCompileWithTracing( true );
        this.configuration.setValidationWarnings( true );
        new XsltFunctionLibrary( this.urlService ).registerAll( this.configuration );
    }

    private TransformerFactory createTransformerFactory()
    {
        return new TransformerFactoryImpl( this.configuration );
    }

    public XsltProcessor newProcessor()
    {
        final TransformerFactory factory = createTransformerFactory();
        return new XsltProcessor( factory );
    }
}
