package com.enonic.xp.lib.repo;

import java.util.function.Supplier;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class RefreshHandler
    implements ScriptBean
{
    private Supplier<NodeService> nodeService;

    private String mode;

    private String repoId;

    private String branch;

    public void setMode( final String mode )
    {
        this.mode = mode;
    }

    public void setRepoId( final String repoId )
    {
        this.repoId = repoId;
    }

    public void setBranch( final String branch )
    {
        this.branch = branch;
    }

    public void refresh()
    {
        createContext().runWith( this::doRefresh );
    }

    private Context createContext()
    {
        final ContextBuilder builder = ContextBuilder.from( ContextAccessor.current() );

        if ( this.branch != null )
        {
            builder.branch( branch );
        }

        if ( this.repoId != null )
        {
            builder.repositoryId( repoId );
        }

        return builder.build();
    }

    private void doRefresh()
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
