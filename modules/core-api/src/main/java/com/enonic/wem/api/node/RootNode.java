package com.enonic.wem.api.node;

import java.time.Instant;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.index.ChildOrder;
import com.enonic.wem.api.security.RoleKeys;
import com.enonic.wem.api.security.acl.AccessControlList;

public class RootNode
    extends Node
{
    public final static NodeId UUID = NodeId.from( "000-000-000-000" );

    private RootNode( final Builder builder )
    {
        super( new Node.Builder().
            id( UUID ).
            creator( RoleKeys.ADMIN ).
            createdTime( Instant.now() ).
            parentPath( null ).
            name( RootNodeName.create() ).
            permissions( builder.permissions ).
            childOrder( builder.childOrder ) );
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public NodePath path()
    {
        return NodePath.ROOT;
    }

    public static class Builder
    {
        private AccessControlList permissions;

        private ChildOrder childOrder;

        public Builder childOrder( final ChildOrder childOrder )
        {
            this.childOrder = childOrder;
            return this;
        }

        public Builder permissions( final AccessControlList permissions )
        {
            this.permissions = permissions;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( this.childOrder );
            Preconditions.checkNotNull( this.permissions );
        }

        public RootNode build()
        {
            validate();
            return new RootNode( this );
        }

    }


}
