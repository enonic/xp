package com.enonic.xp.node;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.query.expr.FieldOrderExpr;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;

@PublicApi
public class CreateRootNodeParams
{
    private final ChildOrder childOrder;

    private final AccessControlList permissions;

    private CreateRootNodeParams( Builder builder )
    {
        Preconditions.checkNotNull( builder.permissions, "Missing permissions for root node" );
        Preconditions.checkArgument( !builder.permissions.isEmpty(), "Missing permissions for root node" );
        childOrder = builder.childOrder;
        permissions = builder.permissions;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ChildOrder getChildOrder()
    {
        return childOrder;
    }

    public AccessControlList getPermissions()
    {
        return permissions;
    }

    public static final class Builder
    {
        private ChildOrder childOrder = ChildOrder.create().
            add( FieldOrderExpr.create( NodeIndexPath.NAME, OrderExpr.Direction.ASC ) ).
            build();

        private AccessControlList permissions = AccessControlList.of( AccessControlEntry.create().
            allowAll().
            principal( RoleKeys.CONTENT_MANAGER_ADMIN ).
            build(), AccessControlEntry.create().
            allowAll().
            principal( PrincipalKey.ofAnonymous() ).
            build() );

        private Builder()
        {
        }

        public Builder childOrder( ChildOrder childOrder )
        {
            this.childOrder = childOrder;
            return this;
        }

        public Builder permissions( AccessControlList permissions )
        {
            this.permissions = permissions;
            return this;
        }

        public CreateRootNodeParams build()
        {
            return new CreateRootNodeParams( this );
        }
    }
}
