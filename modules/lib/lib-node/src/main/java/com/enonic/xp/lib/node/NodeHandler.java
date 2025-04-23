package com.enonic.xp.lib.node;

import java.util.List;
import java.util.Optional;

import com.google.common.io.ByteSource;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.context.Context;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.lib.value.ScriptValueTranslator;
import com.enonic.xp.lib.value.ScriptValueTranslatorResult;
import com.enonic.xp.node.ApplyPermissionsScope;
import com.enonic.xp.node.NodeCommitId;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.NodeVersionId;
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
        return execute( CreateNodeHandler.create()
                            .nodeService( this.nodeService )
                            .params( params )
                            .build() );
    }

    @SuppressWarnings("unused")
    public Object update( final ScriptValue editor, String key )
    {
        return execute( UpdateNodeHandler.create().nodeService( this.nodeService ).key( NodeKey.from( key ) ).editor( editor ).build() );
    }

    @SuppressWarnings("unused")
    public Object setChildOrder( final String key, final String childOrder )
    {
        return execute( SetChildOrderHandler.create()
                            .nodeService( this.nodeService )
                            .key( NodeKey.from( key ) )
                            .childOrder( ChildOrder.from( childOrder ) )
                            .build() );
    }

    @SuppressWarnings("unused")
    public Object get( final GetNodeHandlerParams params )
    {
        return execute( GetNodeHandler.create().nodeService( this.nodeService ).keys( NodeKeys.from( params.getKeys() ) ).build() );
    }

    @SuppressWarnings("unused")
    public Object delete( final String[] keys )
    {
        return execute( DeleteNodeHandler.create()
                            .nodeService( this.nodeService )
                            .keys( NodeKeys.from( keys ) )
                            .build() );
    }

    @SuppressWarnings("unused")
    public Object push( final PushNodeHandlerParams params )
    {
        final PushNodeHandler handler = PushNodeHandler.create()
            .nodeService( this.nodeService )
            .exclude( params.getExclude() )
            .includeChildren( params.isIncludeChildren() )
            .key( params.getKey() )
            .keys( params.getKeys() )
            .resolve( params.isResolve() )
            .targetBranch( params.getTargetBranch() )
            .build();

        return execute( handler );
    }

    public Object patch( final PatchNodeHandlerParams params )
    {
        return execute( PatchNodeHandler.create()
                            .nodeService( this.nodeService )
                            .key( params.getKey() )
                            .branches( params.getBranches() )
                            .editor( params.getEditor() )
                            .build() );
    }

    @SuppressWarnings("unused")
    public Object diff( final DiffBranchesHandlerParams params )
    {
        return execute( DiffBranchesHandler.create()
                            .includeChildren( params.isIncludeChildren() )
                            .key( params.getKey() )
                            .targetBranch( params.getTargetBranch() )
                            .nodeService( this.nodeService )
                            .build() );
    }

    @SuppressWarnings("unused")
    public Object move( final String source, final String target )
    {
        return execute(
            MoveNodeHandler.create().source( NodeKey.from( source ) ).target( target ).nodeService( this.nodeService ).build() );
    }

    @SuppressWarnings("unused")
    public Object query( final QueryNodeHandlerParams params )
    {
        return execute( FindNodesByQueryHandler.create()
                            .query( params.getQuery() )
                            .aggregations( params.getAggregations() )
                            .suggestions( params.getSuggestions() )
                            .highlight( params.getHighlight() )
                            .count( params.getCount() )
                            .start( params.getStart() )
                            .sort( params.getSort() )
                            .filters( params.getFilters() )
                            .explain( params.isExplain() )
                            .nodeService( this.nodeService )
                            .build() );
    }

    @SuppressWarnings("unused")
    public Object exist( final String key )
    {
        return execute( NodeExistsHandler.create().nodeService( this.nodeService ).key( NodeKey.from( key ) ).build() );
    }

    @SuppressWarnings("unused")
    public Object findVersions( final FindVersionsHandlerParams params )
    {
        return execute( FindVersionsHandler.create()
                            .nodeService( this.nodeService )
                            .key( NodeKey.from( params.getKey() ) )
                            .from( params.getStart() )
                            .size( params.getCount() )
                            .build() );
    }

    @SuppressWarnings("unused")
    public Object getActiveVersion( final String key )
    {
        return execute( GetActiveVersionHandler.create().nodeService( this.nodeService ).key( NodeKey.from( key ) ).build() );
    }

    @SuppressWarnings("unused")
    public Object setActiveVersion( final String key, final String versionId )
    {
        return execute( SetActiveVersionHandler.create()
                            .nodeService( this.nodeService )
                            .key( NodeKey.from( key ) )
                            .versionId( NodeVersionId.from( versionId ) )
                            .build() );
    }

    @SuppressWarnings("unused")
    public Object findChildren( final FindChildrenHandlerParams params )
    {
        return execute( FindChildrenNodeHandler.create()
                            .parentKey( NodeKey.from( params.getParentKey() ) )
                            .count( params.getCount() )
                            .start( params.getStart() )
                            .childOrder( ChildOrder.from( params.getChildOrder() ) )
                            .countOnly( params.isCountOnly() )
                            .recursive( params.isRecursive() )
                            .nodeService( this.nodeService )
                            .build() );
    }

    @SuppressWarnings("unused")
    public Object commit( final String[] keys, final String message )
    {
        return execute(
            CommitNodeHandler.create().nodeService( this.nodeService ).keys( NodeKeys.from( keys ) ).message( message ).build() );
    }

    @SuppressWarnings("unused")
    public Object getCommit( final String id )
    {
        return execute( GetCommitHandler.create().nodeService( this.nodeService ).id( NodeCommitId.from( id ) ).build() );
    }

    private static AccessControlList getAccessControlList( final ScriptValue permissions )
    {
        if ( permissions == null )
        {
            return AccessControlList.empty();
        }

        final ScriptValueTranslatorResult translatorResult = new ScriptValueTranslator( false ).create( permissions );

        final PropertyTree asPropertyTree = translatorResult.getPropertyTree();
        final Iterable<PropertySet> asPropertySets = asPropertyTree.getSets( "_permissions" );

        if ( asPropertySets == null )
        {
            throw new IllegalArgumentException( "Did not find parameter [_permissions]" );
        }

        return new PermissionsFactory( asPropertySets ).create();
    }

    @SuppressWarnings("unused")
    @Deprecated
    public Object setRootPermissions( final ScriptValue value )
    {
        final AccessControlList permissions = getAccessControlList( value );

        return execute( SetRootPermissionsHandler.create().permissions( permissions ).nodeService( this.nodeService ).build() );
    }

    @SuppressWarnings("unused")
    public ByteSource getBinary( final String key, final String binaryReference )
    {
        return this.context.callWith( () -> GetBinaryHandler.create()
            .key( NodeKey.from( key ) )
            .binaryReference( binaryReference )
            .nodeService( this.nodeService )
            .build()
            .execute() );
    }

    @SuppressWarnings("unused")
    public void refresh( final String mode )
    {
        this.context.runWith( () -> nodeService.refresh( RefreshMode.valueOf( mode ) ) );
    }

    @SuppressWarnings("unused")
    public Object duplicate( final DuplicateNodeHandlerParams params )
    {
        return execute( DuplicateNodeHandler.create()
                            .nodeId( params.nodeId() )
                            .name( params.name() )
                            .parent( params.parent() )
                            .refresh( params.refresh() )
                            .dataProcessor( params.dataProcessor() )
                            .includeChildren( params.includeChildren() )
                            .nodeService( this.nodeService )
                            .build() );
    }

    @SuppressWarnings("unused")
    public Object applyPermissions( final String key, final ScriptValue permissions, final ScriptValue addPermissions,
                                    final ScriptValue removePermissions, final List<String> branches, final String scope )
    {
        final AccessControlList permissionsEntries = getAccessControlList( permissions );
        final AccessControlList addPermissionsEntries = getAccessControlList( addPermissions );
        final AccessControlList removePermissionsEntries = getAccessControlList( removePermissions );

        return execute( ApplyPermissionsHandler.create()
                            .nodeKey( NodeKey.from( key ) )
                            .branches( Optional.ofNullable( branches )
                                           .map( b -> b.stream().map( Branch::from ).collect( Branches.collecting() ) )
                                           .orElse( Branches.empty() ) )
                            .scope( Optional.ofNullable( scope ).map( ApplyPermissionsScope::valueOf ).orElse( null ) )
                            .permissions( permissionsEntries )
                            .addPermissions( addPermissionsEntries )
                            .removePermissions( removePermissionsEntries )
                            .nodeService( this.nodeService )
                            .build() );
    }

    private Object execute( final AbstractNodeHandler handler )
    {
        return this.context.callWith( handler::execute );
    }
}

