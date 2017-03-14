package com.enonic.xp.core.impl.issue.serializer;

import java.util.stream.Collectors;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.issue.CreateIssueParams;
import com.enonic.xp.issue.Issue;
import com.enonic.xp.issue.IssueStatus;
import com.enonic.xp.security.PrincipalKey;

import static com.enonic.xp.issue.IssuePropertyNames.APPROVERS;
import static com.enonic.xp.issue.IssuePropertyNames.CREATED_TIME;
import static com.enonic.xp.issue.IssuePropertyNames.CREATOR;
import static com.enonic.xp.issue.IssuePropertyNames.DESCRIPTION;
import static com.enonic.xp.issue.IssuePropertyNames.ITEMS;
import static com.enonic.xp.issue.IssuePropertyNames.MODIFIED_TIME;
import static com.enonic.xp.issue.IssuePropertyNames.MODIFIER;
import static com.enonic.xp.issue.IssuePropertyNames.STATUS;
import static com.enonic.xp.issue.IssuePropertyNames.TITLE;

public class IssueDataSerializer
{
    public PropertyTree toCreateNodeData( final CreateIssueParams params )
    {
        final PropertyTree propertyTree = new PropertyTree();

        final PropertySet issueAsData = propertyTree.getRoot();

        issueAsData.ifNotNull().addString( TITLE, params.getTitle() );
        issueAsData.ifNotNull().addInstant( CREATED_TIME, params.getCreatedTime() );
        issueAsData.ifNotNull().addInstant( MODIFIED_TIME, params.getModifiedTime() );
        issueAsData.ifNotNull().addString( CREATOR, params.getCreator().toString() );
        issueAsData.ifNotNull().addString( STATUS, params.getStatus().toString() );
        issueAsData.addString( DESCRIPTION, params.getDescription() );

        if ( params.getApproverIds().getSize() > 0 )
        {
            issueAsData.addStrings( APPROVERS, params.getApproverIds().
                stream().map( approver -> approver.toString() ).collect( Collectors.toList() ) );
        }

        if ( params.getItemIds().getSize() > 0 )
        {
            issueAsData.addStrings( ITEMS, params.getItemIds().asStrings() );
        }

        return propertyTree;
    }

    public PropertyTree toUpdateNodeData( final Issue editedIssue )
    {
        final PropertyTree propertyTree = new PropertyTree();

        final PropertySet issueAsData = propertyTree.getRoot();

        issueAsData.ifNotNull().addString( TITLE, editedIssue.getTitle() );
        issueAsData.ifNotNull().addInstant( CREATED_TIME, editedIssue.getCreatedTime() );
        issueAsData.ifNotNull().addInstant( MODIFIED_TIME, editedIssue.getModifiedTime() );
        issueAsData.ifNotNull().addString( CREATOR, editedIssue.getCreator().toString() );
        issueAsData.ifNotNull().addString( MODIFIER, editedIssue.getModifier().toString() );
        issueAsData.ifNotNull().addString( STATUS, editedIssue.getStatus().toString() );
        issueAsData.addString( DESCRIPTION, editedIssue.getDescription() );

        issueAsData.addStrings( APPROVERS, editedIssue.getApproverIds().
            stream().map( approver -> approver.toString() ).collect( Collectors.toList() ) );

        issueAsData.addStrings( ITEMS, editedIssue.getItemIds().asStrings() );

        return propertyTree;
    }

    public Issue.Builder fromData( final PropertySet issueProperties )
    {
        final Issue.Builder builder = Issue.create();

        builder.title( issueProperties.getString( TITLE ) );
        builder.description( issueProperties.getString( DESCRIPTION ) );
        builder.status( IssueStatus.valueOf( issueProperties.getString( STATUS ) ) );

        extractUserInfo( issueProperties, builder );
        extractApprovers( issueProperties, builder );
        extractItems( issueProperties, builder );

        return builder;
    }

    private void extractApprovers( final PropertySet issueProperties, final Issue.Builder builder )
    {
        for ( String approver : issueProperties.getStrings( APPROVERS ) )
        {
            builder.addApproverId( PrincipalKey.from( approver ) );
        }
    }

    private void extractItems( final PropertySet issueProperties, final Issue.Builder builder )
    {
        for ( String item : issueProperties.getStrings( ITEMS ) )
        {
            builder.addItemId( ContentId.from( item ) );
        }
    }

    private void extractUserInfo( final PropertySet issueProperties, final Issue.Builder builder )
    {
        builder.creator( PrincipalKey.from( issueProperties.getString( CREATOR ) ) );
        builder.createdTime( issueProperties.getInstant( CREATED_TIME ) );
        builder.modifier(
            issueProperties.getString( MODIFIER ) != null ? PrincipalKey.from( issueProperties.getString( MODIFIER ) ) : null );
        builder.modifiedTime( issueProperties.getInstant( MODIFIED_TIME ) != null ? issueProperties.getInstant( MODIFIED_TIME ) : null );
    }
}
