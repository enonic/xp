package com.enonic.xp.issue;

import com.enonic.xp.content.ContentIndexPath;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeType;
import com.enonic.xp.query.expr.FieldOrderExpr;
import com.enonic.xp.query.expr.OrderExpr;

public final class IssueConstants
{
    public static final String ISSUE_ROOT_NAME = "issues";

    public static final NodePath ISSUE_ROOT_PARENT = NodePath.ROOT;

    public static final NodePath ISSUE_ROOT_PATH = new NodePath( ISSUE_ROOT_PARENT, NodeName.from( ISSUE_ROOT_NAME ) );

    public static final NodeType ISSUE_NODE_COLLECTION = NodeType.from( "issue" );

    public static final ChildOrder DEFAULT_CHILD_ORDER = ChildOrder.create().
        add( FieldOrderExpr.create( ContentIndexPath.MODIFIED_TIME, OrderExpr.Direction.DESC ) ).
        build();

    private IssueConstants()
    {
    }
}
