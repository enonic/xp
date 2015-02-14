package com.enonic.xp.core.node;

import org.junit.Test;

import com.enonic.xp.core.index.ChildOrder;
import com.enonic.xp.core.query.expr.FieldOrderExpr;
import com.enonic.xp.core.query.expr.OrderExpr;
import com.enonic.xp.core.security.RoleKeys;
import com.enonic.xp.core.security.acl.AccessControlEntry;
import com.enonic.xp.core.security.acl.AccessControlList;

import static org.junit.Assert.*;

public class RootNodeTest
{
    @Test
    public void create()
        throws Exception
    {
        final RootNode rootNode = RootNode.create().
            childOrder( ChildOrder.create().
                add( FieldOrderExpr.create( NodeIndexPath.NAME, OrderExpr.Direction.ASC ) ).
                build() ).
            permissions( AccessControlList.of( AccessControlEntry.create().
                allowAll().
                principal( RoleKeys.CONTENT_MANAGER_ADMIN ).
                build() ) ).
            build();

        assertEquals( "", rootNode.name().toString() );
        assertEquals( null, rootNode.parentPath() );
        assertEquals( NodePath.newPath( NodePath.ROOT ).addElement( "" ).build(), rootNode.path() );
    }
}