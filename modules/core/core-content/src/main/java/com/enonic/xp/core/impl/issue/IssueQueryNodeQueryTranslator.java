package com.enonic.xp.core.impl.issue;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.issue.IssueConstants;
import com.enonic.xp.issue.IssueQuery;
import com.enonic.xp.issue.IssueStatus;
import com.enonic.xp.issue.IssueType;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.SearchMode;
import com.enonic.xp.query.filter.BooleanFilter;
import com.enonic.xp.query.filter.ExistsFilter;
import com.enonic.xp.query.filter.Filter;
import com.enonic.xp.query.filter.ValueFilter;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;

import static com.enonic.xp.issue.IssuePropertyNames.APPROVERS;
import static com.enonic.xp.issue.IssuePropertyNames.CREATOR;
import static com.enonic.xp.issue.IssuePropertyNames.PUBLISH_REQUEST_ITEM_ID;
import static com.enonic.xp.issue.IssuePropertyNames.STATUS;
import static com.enonic.xp.issue.IssuePropertyNames.TYPE;
import static java.util.stream.Collectors.toList;

final class IssueQueryNodeQueryTranslator
{
    public static NodeQuery translate( final IssueQuery issueQuery )
    {
        final NodeQuery.Builder builder = NodeQuery.create();

        final ValueFilter issueCollectionFilter = ValueFilter.create().
            fieldName( NodeIndexPath.NODE_TYPE.getPath() ).
            addValue( ValueFactory.newString( IssueConstants.ISSUE_NODE_COLLECTION.getName() ) ).
            build();

        if ( issueQuery.isCount() )
        {
            builder.searchMode( SearchMode.COUNT );
        }

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
        if ( approvers != null && !approvers.isEmpty() )
        {
            builder.addQueryFilter( ValueFilter.create().
                fieldName( APPROVERS ).
                addValues( approvers.stream().map( PrincipalKey::toString ).collect( toList() ) ).
                build() );
        }

        final ContentIds items = issueQuery.getItems();
        if ( items != null && !items.isEmpty() )
        {
            builder.addQueryFilter( ValueFilter.create().
                fieldName( PUBLISH_REQUEST_ITEM_ID ).
                addValues( items.stream().map( ContentId::toString ).collect( toList() ) ).
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

        final IssueType type = issueQuery.getType();
        if ( type != null )
        {
            final Filter isOfType = ValueFilter.create().
                fieldName( TYPE ).
                addValues( type.toString() ).
                build();

            if ( type == IssueType.STANDARD )
            {
                final Filter notExists = BooleanFilter.create().
                    mustNot( ExistsFilter.create().fieldName( TYPE ).build() ).
                    build();

                final Filter isStandard = BooleanFilter.create().
                    should( isOfType ).
                    should( notExists ).
                    build();

                builder.addQueryFilter( isStandard );
            }
            else
            {
                builder.addQueryFilter( isOfType );
            }
        }

        builder.setOrderExpressions( IssueConstants.DEFAULT_CHILD_ORDER.getOrderExpressions() );

        return builder.build();
    }
}
