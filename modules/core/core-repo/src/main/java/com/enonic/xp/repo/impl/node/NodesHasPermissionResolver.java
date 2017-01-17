package com.enonic.xp.repo.impl.node;

import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.index.IndexPath;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.SearchMode;
import com.enonic.xp.query.filter.IdFilter;
import com.enonic.xp.query.filter.ValueFilter;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.Permission;

public class NodesHasPermissionResolver
    extends AbstractNodeCommand
{
    private final NodeIds nodeIds;

    private final Permission permission;

    private NodesHasPermissionResolver( final Builder builder )
    {
        super( builder );
        nodeIds = builder.nodeIds;
        permission = builder.permission;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final AbstractNodeCommand source )
    {
        return new Builder( source );
    }

    public boolean execute()
    {
        final Context context = ContextAccessor.current();

        if ( context.getAuthInfo().hasRole( RoleKeys.ADMIN ) )
        {
            return true;
        }

        if ( nodeIds.isEmpty() )
        {
            return false;
        }

        final NodeQuery query = NodeQuery.create().
            addQueryFilter( IdFilter.create().
                fieldName( NodeIndexPath.ID.getPath() ).
                values( nodeIds ).
                build() ).
            addQueryFilter( ValueFilter.create().
                fieldName( getPermissionFieldName().getPath() ).
                addValues( context.getAuthInfo().getPrincipals().stream().
                    map( PrincipalKey::toString ).
                    collect( Collectors.toList() ) ).
                build() ).
            searchMode( SearchMode.COUNT ).
            build();

        final FindNodesByQueryResult result = FindNodesByQueryCommand.create( this ).
            query( query ).
            build().
            execute();

        return result.getTotalHits() == nodeIds.getSize();
    }

    private IndexPath getPermissionFieldName()
    {

        switch ( permission )
        {
            case CREATE:
                return NodeIndexPath.PERMISSIONS_CREATE;
            case READ:
                return NodeIndexPath.PERMISSIONS_READ;
            case MODIFY:
                return NodeIndexPath.PERMISSIONS_MODIFY;
            case DELETE:
                return NodeIndexPath.PERMISSIONS_DELETE;
            case PUBLISH:
                return NodeIndexPath.PERMISSIONS_PUBLISH;
            case READ_PERMISSIONS:
                return NodeIndexPath.PERMISSIONS_READ_PERMISSION;
            case WRITE_PERMISSIONS:
                return NodeIndexPath.PERMISSIONS_WRITE_PERMISSION;
            default:
                throw new IllegalArgumentException( "Unexpected value for querying permission: [" + permission.name() + "]" );
        }

    }

    public static final class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private NodeIds nodeIds;

        private Permission permission;

        private Builder()
        {
        }

        private Builder( final AbstractNodeCommand source )
        {
            super( source );
        }

        public Builder nodeIds( final NodeIds val )
        {
            nodeIds = val;
            return this;
        }

        public Builder permission( final Permission val )
        {
            permission = val;
            return this;
        }

        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( this.permission, "Permission must be set" );
        }

        public NodesHasPermissionResolver build()
        {
            validate();
            return new NodesHasPermissionResolver( this );
        }
    }
}
