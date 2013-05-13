package com.enonic.wem.web.jsp;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.enonic.wem.core.index.IndexService;
import com.enonic.wem.core.initializer.StartupInitializer;

public final class JspDataTools
{
    private final static Logger LOG = LoggerFactory.getLogger( JspDataTools.class );

    private final ApplicationContext context;

    public JspDataTools( final ApplicationContext context )
    {
        this.context = context;
    }

    public void reindexData()
    {
        try
        {
            this.context.getBean( IndexService.class ).reIndex();
        }
        catch ( final Exception e )
        {
            LOG.error( e.getMessage(), e );
        }
    }

    public void cleanData()
    {
        try
        {
            this.context.getBean( StartupInitializer.class ).initialize( true );
            this.context.getBean( IndexService.class ).reIndex();
        }
        catch ( final Exception e )
        {
            LOG.error( e.getMessage(), e );
        }
    }

    public static JspDataTools create( final ServletContext servletContext )
    {
        return new JspDataTools( WebApplicationContextUtils.getRequiredWebApplicationContext( servletContext ) );
    }
}
