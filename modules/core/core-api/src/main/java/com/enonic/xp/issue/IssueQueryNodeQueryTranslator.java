package com.enonic.xp.issue;

import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.query.filter.ValueFilter;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;

import static com.enonic.xp.issue.IssuePropertyNames.APPROVERS;
import static com.enonic.xp.issue.IssuePropertyNames.CREATOR;
import static com.enonic.xp.issue.IssuePropertyNames.STATUS;
import static java.util.stream.Collectors.toList;

public final class IssueQueryNodeQueryTranslator
{
    public static NodeQuery translate( final IssueQuery issueQuery )
    {
        final NodeQuery.Builder builder = NodeQuery.create();

        final ValueFilter issueCollectionFilter = ValueFilter.create().
            fieldName( NodeIndexPath.NODE_TYPE.getPath() ).
            addValue( ValueFactory.newString( IssueConstants.ISSUE_NODE_COLLECTION.getName() ) ).
            build();

        builder.
            from( issueQuery.getFrom() ).
            size( issueQuery.getSize() ).
            addQueryFilter( issueCollectionFilter );

        final PrincipalKey creator = issueQuery.getCreator();
        if ( creator != null )
        {
            builder.addQueryFilter( ValueFilter.create().
                fieldName( CREATOR ).
                addValues( creator.toString() ).
                build() );
        }

        final PrincipalKeys approvers = issueQuery.getApprovers();
        if ( approvers != null && approvers.isNotEmpty() )
        {
            builder.addQueryFilter( ValueFilter.create().
                fieldName( APPROVERS ).
                addValues( approvers.stream().map( PrincipalKey::toString ).collect( toList() ) ).
                build() );
        }

        final IssueStatus status = issueQuery.getStatus();
        if ( status != null )
        {
            builder.addQueryFilter( ValueFilter.create().
                fieldName( STATUS ).
                addValues( status.toString() ).
                build() );
        }

        builder.setOrderExpressions( IssueConstants.DEFAULT_CHILD_ORDER.getOrderExpressions() );

        return builder.build();
    }
}
