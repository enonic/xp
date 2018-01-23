package com.enonic.xp.core.impl.issue;

import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.issue.IssueCommentQuery;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.SearchMode;
import com.enonic.xp.query.filter.ValueFilter;
import com.enonic.xp.security.PrincipalKey;

import static com.enonic.xp.core.impl.issue.IssueCommentPropertyNames.CREATOR;

final class IssueCommentQueryNodeQueryTranslator
{
    public static NodeQuery translate( final IssueCommentQuery issueCommentQuery, NodeName parentName )
    {
        final NodeQuery.Builder builder = NodeQuery.create();

        builder.parent( NodePath.create( IssueConstants.ISSUE_ROOT_PATH, parentName.toString() ).build() );

        final ValueFilter issueCommentsCollectionFilter = ValueFilter.create().
            fieldName( NodeIndexPath.NODE_TYPE.getPath() ).
            addValue( ValueFactory.newString( IssueCommentConstants.NODE_COLLECTION.getName() ) ).
            build();

        if ( issueCommentQuery.isCount() )
        {
            builder.searchMode( SearchMode.COUNT );
        }

        builder.
            from( issueCommentQuery.getFrom() ).
            size( issueCommentQuery.getSize() ).
            addQueryFilter( issueCommentsCollectionFilter );

        final PrincipalKey creator = issueCommentQuery.getCreator();
        if ( creator != null )
        {
            builder.addQueryFilter( ValueFilter.create().
                fieldName( CREATOR ).
                addValues( creator.toString() ).
                build() );
        }

        builder.setOrderExpressions( issueCommentQuery.getOrder().getOrderExpressions() );

        return builder.build();
    }
}
