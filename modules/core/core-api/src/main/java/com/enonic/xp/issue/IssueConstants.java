package com.enonic.xp.issue;

import com.enonic.xp.content.ContentIndexPath;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeType;
import com.enonic.xp.query.expr.FieldOrderExpr;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;

public class IssueConstants
{

    public static final String ISSUE_ROOT_NAME = "issues";

    public static final NodePath ISSUE_ROOT_PARENT = NodePath.ROOT;

    public static final NodePath ISSUE_ROOT_PATH = NodePath.create( ISSUE_ROOT_PARENT, ISSUE_ROOT_NAME ).build();

    public static final NodeType ISSUE_NODE_COLLECTION = NodeType.from( "issue" );

    public static final ChildOrder DEFAULT_CHILD_ORDER = ChildOrder.create().
        add( FieldOrderExpr.create( ContentIndexPath.MODIFIED_TIME, OrderExpr.Direction.DESC ) ).
        build();

    public static final AccessControlList ACCESS_CONTROL_ENTRIES =
        AccessControlList.of( AccessControlEntry.create().principal( RoleKeys.CONTENT_MANAGER_ADMIN ).allowAll().build() );
}
