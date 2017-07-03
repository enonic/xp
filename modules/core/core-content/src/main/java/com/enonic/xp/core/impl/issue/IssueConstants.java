package com.enonic.xp.core.impl.issue;

import com.enonic.xp.content.ContentIndexPath;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeType;
import com.enonic.xp.query.expr.FieldOrderExpr;
import com.enonic.xp.query.expr.OrderExpr;

final class IssueConstants
{

    static final String ISSUE_ROOT_NAME = "issues";

    static final NodePath ISSUE_ROOT_PARENT = NodePath.ROOT;

    static final NodePath ISSUE_ROOT_PATH = NodePath.create( ISSUE_ROOT_PARENT, ISSUE_ROOT_NAME ).build();

    static final NodeType ISSUE_NODE_COLLECTION = NodeType.from( "issue" );

    static final ChildOrder DEFAULT_CHILD_ORDER = ChildOrder.create().
        add( FieldOrderExpr.create( ContentIndexPath.MODIFIED_TIME, OrderExpr.Direction.DESC ) ).
        build();
}
