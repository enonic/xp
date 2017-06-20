package com.enonic.xp.core.impl.issue;

import java.util.List;

import org.codehaus.jparsec.util.Lists;

import com.enonic.xp.core.impl.issue.serializer.IssueDataSerializer;
import com.enonic.xp.issue.Issue;
import com.enonic.xp.issue.IssueId;
import com.enonic.xp.issue.IssueName;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.Nodes;

public class IssueNodeTranslator
{
    private static final IssueDataSerializer ISSUE_DATA_SERIALIZER = new IssueDataSerializer();

    public static Issue fromNode( final Node node )
    {
        return doTranslate( node );
    }

    public static List<Issue> fromNodes( final Nodes nodes )
    {
        List<Issue> issues = Lists.arrayList();

        for ( final Node node : nodes )
        {
            issues.add( doTranslate( node ) );
        }

        return issues;
    }

    private static Issue doTranslate( final Node node )
    {
        final Issue.Builder builder = ISSUE_DATA_SERIALIZER.fromData( node.data().getRoot() );
        builder.id( IssueId.from( node.id().toString() ) );
        builder.name( IssueName.from( node.name().toString() ) );

        return builder.build();
    }
}
