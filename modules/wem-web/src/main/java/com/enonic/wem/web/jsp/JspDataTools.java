package com.enonic.wem.web.jsp;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.enonic.wem.core.index.IndexService;
import com.enonic.wem.core.initializer.StartupInitializer;

@Component
public final class JspDataTools
{
    private final static Logger LOG = LoggerFactory.getLogger( JspDataTools.class );

    private static JspDataTools INSTANCE;

    private final IndexService indexService;

    private final StartupInitializer startupInitializer;

    @Inject
    public JspDataTools( final IndexService indexService, final StartupInitializer startupInitializer )
    {
        INSTANCE = this;
        this.indexService = indexService;
        this.startupInitializer = startupInitializer;
    }

    public void reindexData()
    {
        try
        {
            this.indexService.reIndex();
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
            this.startupInitializer.initialize( true );
            this.indexService.reIndex();
        }
        catch ( final Exception e )
        {
            LOG.error( e.getMessage(), e );
        }
    }

    public static JspDataTools get()
    {
        return INSTANCE;
    }
}
