package com.enonic.wem.xslt.internal;

import javax.xml.transform.TransformerFactory;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import net.sf.saxon.Configuration;
import net.sf.saxon.TransformerFactoryImpl;

import com.enonic.wem.portal.view.ViewFunctions;
import com.enonic.wem.xslt.XsltProcessor;
import com.enonic.wem.xslt.XsltProcessorFactory;
import com.enonic.wem.xslt.internal.function.XsltFunctionLibrary;

@Component
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

    @Reference
    public void setViewFunctions( final ViewFunctions functions )
    {
        new XsltFunctionLibrary( functions ).registerAll( this.configuration );
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
