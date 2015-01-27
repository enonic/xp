package com.enonic.wem.api.node;

import java.time.Instant;

import com.enonic.wem.api.index.ChildOrder;
import com.enonic.wem.api.query.expr.FieldOrderExpr;
import com.enonic.wem.api.query.expr.OrderExpr;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.RoleKeys;
import com.enonic.wem.api.security.acl.AccessControlEntry;
import com.enonic.wem.api.security.acl.AccessControlList;

public class RootNode
    extends Node
{
    public final static NodeId UUID = NodeId.from( "000-000-000-000" );

    private RootNode( final Builder builder )
    {
        super( new Node.Builder().
            id( UUID ).
            creator( RoleKeys.CONTENT_MANAGER ).
            createdTime( Instant.now() ).
            parentPath( NodePath.ROOT ).
            name( RootNodeName.create() ).
            permissions( builder.permissions ).
            childOrder( builder.childOrder ) );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private AccessControlList permissions = AccessControlList.of( AccessControlEntry.create().
            allowAll().
            principal( RoleKeys.CONTENT_MANAGER ).
            build(), AccessControlEntry.create().
            allowAll().
            principal( PrincipalKey.ofAnonymous() ).
            build() );

        private ChildOrder childOrder = ChildOrder.create().
            add( FieldOrderExpr.create( NodeIndexPath.NAME, OrderExpr.Direction.ASC ) ).
            build();

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

        public RootNode build()
        {
            return new RootNode( this );
        }

    }


}
