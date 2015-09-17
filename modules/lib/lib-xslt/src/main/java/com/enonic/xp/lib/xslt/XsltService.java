package com.enonic.xp.lib.xslt;

import java.util.function.Supplier;

import javax.xml.transform.TransformerFactory;

import net.sf.saxon.Configuration;
import net.sf.saxon.TransformerFactoryImpl;

import com.enonic.xp.lib.xslt.function.XsltFunctionLibrary;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.portal.view.ViewFunctionService;
import com.enonic.xp.resource.ResourceService;

public final class XsltService
    implements ScriptBean
{
    private final Configuration configuration;

    private ResourceService resourceService;

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
        XsltProcessor processor = new XsltProcessor( factory );
        processor.setResourceService( resourceService );
        return processor;
    }

    public void setViewFunctionService( final Supplier<ViewFunctionService> viewFunctionService )
    {
        new XsltFunctionLibrary( viewFunctionService ).registerAll( this.configuration );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.resourceService = context.getService( ResourceService.class ).get();
        final Supplier<ViewFunctionService> service = context.getService( ViewFunctionService.class );
        setViewFunctionService( service );
    }
}
