package com.enonic.xp.lib.node;

import com.google.common.io.ByteSource;

import com.enonic.xp.context.Context;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.lib.value.ScriptValueTranslator;
import com.enonic.xp.lib.value.ScriptValueTranslatorResult;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.security.acl.AccessControlList;

public class NodeHandler
{
    private final NodeService nodeService;

    private final Context context;

    public NodeHandler( final Context context, final NodeService nodeService )
    {
        this.context = context;
        this.nodeService = nodeService;
    }

    @SuppressWarnings("unused")
    public Object create( final ScriptValue params )
    {
        return execute( CreateNodeHandler.create().
            nodeService( this.nodeService ).
            params( params ).
            build() );
    }

    @SuppressWarnings("unused")
    public Object modify( final ScriptValue editor, String key )
    {
        return execute( ModifyNodeHandler.create().
            nodeService( this.nodeService ).
            key( NodeKey.from( key ) ).
            editor( editor ).
            build() );
    }

    @SuppressWarnings("unused")
    public Object setChildOrder( final String key, final String childOrder )
    {
        return execute( SetChildOrderHandler.create().
            nodeService( this.nodeService ).
            key( NodeKey.from( key ) ).
            childOrder( ChildOrder.from( childOrder ) ).
            build() );
    }

    @SuppressWarnings("unused")
    public Object get( final String[] keys )
    {
        return execute( GetNodeHandler.create().
            nodeService( this.nodeService ).
            keys( NodeKeys.from( keys ) ).
            build() );
    }

    @SuppressWarnings("unused")
    public Object delete( final String[] keys )
    {
        return execute( DeleteNodeHandler.create().
            nodeService( this.nodeService ).
            keys( NodeKeys.from( keys ) ).
            build() );
    }

    @SuppressWarnings("unused")
    public Object push( final PushNodeHandlerParams params )
    {
        final PushNodeHandler handler = PushNodeHandler.create().
            nodeService( this.nodeService ).
            exclude( params.getExclude() ).
            includeChildren( params.isIncludeChildren() ).
            key( params.getKey() ).
            keys( params.getKeys() ).
            resolve( params.isResolve() ).
            targetBranch( params.getTargetBranch() ).
            build();

        return execute( handler );
    }

    @SuppressWarnings("unused")
    public Object diff( final DiffBranchesHandlerParams params )
    {
        return execute( DiffBranchesHandler.create().
            includeChildren( params.isIncludeChildren() ).
            key( params.getKey() ).
            targetBranch( params.getTargetBranch() ).
            nodeService( this.nodeService ).
            build() );
    }

    @SuppressWarnings("unused")
    public Object move( final String source, final String target )
    {
        return execute( MoveNodeHandler.create().
            source( NodeKey.from( source ) ).
            target( target ).
            nodeService( this.nodeService ).
            build() );
    }

    @SuppressWarnings("unused")
    public Object query( final QueryNodeHandlerParams params )
    {
        return execute( FindNodesByQueryHandler.create().
            query( params.getQuery() ).
            aggregations( params.getAggregations() ).
            count( params.getCount() ).
            start( params.getStart() ).
            sort( params.getSort() ).
            filters( params.getFilters() ).
            explain( params.isExplain() ).
            nodeService( this.nodeService ).
            build() );
    }

    @SuppressWarnings("unused")
    public Object findChildren( final FindChildrenHandlerParams params )
    {
        return execute( FindChildrenNodeHandler.create().
            parentKey( NodeKey.from( params.getParentKey() ) ).
            count( params.getCount() ).
            start( params.getStart() ).
            childOrder( ChildOrder.from( params.getChildOrder() ) ).
            countOnly( params.isCountOnly() ).
            recursive( params.isRecursive() ).
            nodeService( this.nodeService ).
            build() );
    }

    @SuppressWarnings("unused")
    public Object setRootPermissions( final ScriptValue value )
    {
        final ScriptValueTranslatorResult translatorResult = new ScriptValueTranslator( false ).create( value );

        final PropertyTree asPropertyTree = translatorResult.getPropertyTree();
        final Iterable<PropertySet> asPropertySets = asPropertyTree.getSets( "_permissions" );

        final boolean inheritPermissions =
            asPropertyTree.getBoolean( "_inheritsPermissions" ) != null ? asPropertyTree.getBoolean( "_inheritsPermissions" ) : true;

        if ( asPropertySets == null )
        {
            throw new IllegalArgumentException( "Did not find parameter [_permissions]" );
        }

        final AccessControlList permissions = new PermissionsFactory( asPropertySets ).create();

        return execute( SetRootPermissionsHandler.create().
            permissions( permissions ).
            inheritPermissions( inheritPermissions ).
            nodeService( this.nodeService ).
            build() );
    }

    @SuppressWarnings("unused")
    public ByteSource getBinary( final String key, final String binaryReference )
    {
        return this.context.callWith( () -> GetBinaryHandler.create().
            key( NodeKey.from( key ) ).
            binaryReference( binaryReference ).
            nodeService( this.nodeService ).
            build().
            execute() );
    }

    @SuppressWarnings("unused")
    public void refresh( final String mode )
    {
        this.context.runWith( () -> nodeService.refresh( RefreshMode.valueOf( mode ) ) );
    }

    private Object execute( final AbstractNodeHandler handler )
    {
        return this.context.callWith( handler::execute );
    }
}

