package com.enonic.xp.lib.node;

import com.enonic.xp.context.Context;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.script.ScriptValue;

public class NodeHandler
{
    protected NodeService nodeService;

    private final Context context;

    public NodeHandler( final Context context, final NodeService nodeService )
    {
        this.context = context;
        this.nodeService = nodeService;
    }

    public Object create( final ScriptValue params )
    {
        final CreateNodeHandler createHandler = CreateNodeHandler.create().
            nodeService( this.nodeService ).
            params( params ).
            build();

        return this.context.callWith( createHandler::execute );
    }

    public Object modify( final ScriptValue editor, String key )
    {
        final ModifyNodeHandler modifyHandler = ModifyNodeHandler.create().
            nodeService( this.nodeService ).
            key( NodeKey.from( key ) ).
            editor( editor ).
            build();

        return this.context.callWith( modifyHandler::execute );
    }

    public Object get( final String key, String[] keys )
    {
        final GetNodeHandler getNodeHandler = GetNodeHandler.create().
            nodeService( this.nodeService ).
            key( NodeKey.from( key ) ).
            keys( NodeKeys.from( keys ) ).
            build();

        return this.context.callWith( getNodeHandler::execute );
    }

    public Object push( final PushNodeHandlerParams params )
    {
        final PushNodeHandler pushNodeHandler = PushNodeHandler.create().
            nodeService( this.nodeService ).
            exclude( params.getExclude() ).
            includeChildren( params.isIncludeChildren() ).
            keys( params.getIds() ).
            resolve( params.isResolve() ).
            targetBranch( params.getTargetBranch() ).
            build();

        return this.context.callWith( pushNodeHandler::execute );
    }

}

