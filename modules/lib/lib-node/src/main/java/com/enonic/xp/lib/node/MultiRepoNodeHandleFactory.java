package com.enonic.xp.lib.node;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public class MultiRepoNodeHandleFactory
    implements ScriptBean
{
    private NodeService nodeService;

    @Override
    public void initialize( final BeanContext context )
    {
        this.nodeService = context.getService( NodeService.class ).get();
    }

    public MultiRepoNodeHandler create( final MultiRepoNodeHandleContext context )
    {
        final ContextBuilder contextBuilder = ContextBuilder.from( ContextAccessor.current() );

        return MultiRepoNodeHandler.create().
            context( ContextAccessor.current() ).
            searchTargets( context.getSearchTargets() ).
            nodeService( this.nodeService ).
            build();
    }

}
