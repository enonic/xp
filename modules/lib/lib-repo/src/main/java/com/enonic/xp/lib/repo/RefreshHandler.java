package com.enonic.xp.lib.repo;

import java.util.function.Supplier;

import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class RefreshHandler
    implements ScriptBean
{
    private Supplier<NodeService> nodeService;

    private String mode;

    public void setMode( final String mode )
    {
        this.mode = mode;
    }

    public void refresh()
    {
        nodeService.get().refresh( refreshMode() );
    }

    private RefreshMode refreshMode()
    {
        if ( mode == null || "all".endsWith( mode ) )
        {
            return RefreshMode.ALL;
        }
        else if ( "search".equals( mode ) )
        {
            return RefreshMode.SEARCH;
        }
        else if ( "storage".equals( mode ) )
        {
            return RefreshMode.STORAGE;
        }

        throw new IllegalArgumentException( "Invalid refresh mode: " + mode );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        nodeService = context.getService( NodeService.class );
    }
}
