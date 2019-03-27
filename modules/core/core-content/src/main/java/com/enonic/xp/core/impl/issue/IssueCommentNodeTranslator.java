package com.enonic.xp.core.impl.issue;

import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.xp.core.impl.issue.serializer.IssueCommentDataSerializer;
import com.enonic.xp.issue.IssueComment;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.Nodes;

public class IssueCommentNodeTranslator
{
    private static final IssueCommentDataSerializer ISSUE_COMMENT_DATA_SERIALIZER = new IssueCommentDataSerializer();

    public static IssueComment fromNode( final Node node )
    {
        return doTranslate( node );
    }

    public static List<IssueComment> fromNodes( final Nodes nodes )
    {
        final List<IssueComment> issues = Lists.newArrayList();

        for ( final Node node : nodes )
        {
            issues.add( doTranslate( node ) );
        }

        return issues;
    }

    private static IssueComment doTranslate( final Node node )
    {
        return ISSUE_COMMENT_DATA_SERIALIZER.fromData( node.data() ).id( node.id() ).build();
    }
}
