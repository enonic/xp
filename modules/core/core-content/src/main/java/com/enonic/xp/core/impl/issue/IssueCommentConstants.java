package com.enonic.xp.core.impl.issue;

import com.enonic.xp.content.ContentIndexPath;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.NodeType;
import com.enonic.xp.query.expr.FieldOrderExpr;
import com.enonic.xp.query.expr.OrderExpr;

final class IssueCommentConstants
{
    static final NodeType NODE_COLLECTION = NodeType.from( "issue_comment" );

    static final ChildOrder DEFAULT_CHILD_ORDER = ChildOrder.create().
        add( FieldOrderExpr.create( ContentIndexPath.MODIFIED_TIME, OrderExpr.Direction.DESC ) ).
        build();
}
