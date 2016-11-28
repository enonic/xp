package com.enonic.xp.lib.node;

import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public abstract class OldBaseNodeHandler
    implements ScriptBean
{
    protected NodeService nodeService;

    public final Object execute()
    {
        return doExecute();
    }

    protected abstract Object doExecute();

    protected <T> T valueOrDefault( final T value, final T defValue )
    {
        return value == null ? defValue : value;
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.nodeService = context.getService( NodeService.class ).get();
    }

    protected Node doGetNode( final NodeKey nodeKey )
    {
        if ( !nodeKey.isId() )
        {
            return nodeService.getByPath( nodeKey.getAsPath() );
        }
        else
        {
            return nodeService.getById( nodeKey.getAsNodeId() );
        }
    }
}
