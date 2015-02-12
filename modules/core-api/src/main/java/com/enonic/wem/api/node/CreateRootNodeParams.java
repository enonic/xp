package com.enonic.wem.api.node;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.index.ChildOrder;
import com.enonic.wem.api.query.expr.FieldOrderExpr;
import com.enonic.wem.api.query.expr.OrderExpr;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.RoleKeys;
import com.enonic.wem.api.security.acl.AccessControlEntry;
import com.enonic.wem.api.security.acl.AccessControlList;

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
