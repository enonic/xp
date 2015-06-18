package com.enonic.xp.lib.xslt;

import javax.xml.transform.TransformerFactory;

import net.sf.saxon.Configuration;
import net.sf.saxon.TransformerFactoryImpl;

import com.enonic.xp.lib.xslt.function.XsltFunctionLibrary;
import com.enonic.xp.portal.view.ViewFunctionService;

public final class XsltService
{
    private Configuration configuration;

    public XsltService()
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

    public XsltProcessor newProcessor()
    {
        final TransformerFactory factory = createTransformerFactory();
        return new XsltProcessor( factory );
    }

    public void setViewFunctionService( final ViewFunctionService viewFunctionService )
    {
        new XsltFunctionLibrary( viewFunctionService ).registerAll( this.configuration );
    }
}
