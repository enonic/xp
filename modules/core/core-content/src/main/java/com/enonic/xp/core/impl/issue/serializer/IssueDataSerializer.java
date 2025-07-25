package com.enonic.xp.core.impl.issue.serializer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.core.impl.issue.PublishRequestPropertyNames;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.issue.CreateIssueParams;
import com.enonic.xp.issue.CreatePublishRequestIssueParams;
import com.enonic.xp.issue.Issue;
import com.enonic.xp.issue.IssueStatus;
import com.enonic.xp.issue.IssueType;
import com.enonic.xp.issue.PublishRequest;
import com.enonic.xp.issue.PublishRequestIssue;
import com.enonic.xp.issue.PublishRequestItem;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.util.Reference;

import static com.enonic.xp.issue.IssuePropertyNames.APPROVERS;
import static com.enonic.xp.issue.IssuePropertyNames.CREATED_TIME;
import static com.enonic.xp.issue.IssuePropertyNames.CREATOR;
import static com.enonic.xp.issue.IssuePropertyNames.DESCRIPTION;
import static com.enonic.xp.issue.IssuePropertyNames.INDEX;
import static com.enonic.xp.issue.IssuePropertyNames.MODIFIED_TIME;
import static com.enonic.xp.issue.IssuePropertyNames.MODIFIER;
import static com.enonic.xp.issue.IssuePropertyNames.PUBLISH_REQUEST;
import static com.enonic.xp.issue.IssuePropertyNames.STATUS;
import static com.enonic.xp.issue.IssuePropertyNames.TITLE;
import static com.enonic.xp.issue.IssuePropertyNames.TYPE;

public class IssueDataSerializer
{
    PublishRequestIssueSerializer publishRequestIssueSerializer = new PublishRequestIssueSerializer();

    public PropertyTree toCreateNodeData( final CreateIssueParams params )
    {
        final PropertyTree propertyTree = new PropertyTree();

        final PropertySet issueAsData = propertyTree.getRoot();

        issueAsData.ifNotNull().addEnum( TYPE, params.getIssueType() );
        issueAsData.ifNotNull().addString( TITLE, params.getTitle() );
        issueAsData.ifNotNull().addString( STATUS, params.getStatus().toString() );
        issueAsData.addString( DESCRIPTION, params.getDescription() );

        if ( !params.getApproverIds().isEmpty() )
        {
            issueAsData.addStrings( APPROVERS, params.getApproverIds().
                stream().map( PrincipalKey::toString ).collect( Collectors.toList() ) );
        }

        if ( params.getPublishRequest() != null )
        {
            addPublishRequest( issueAsData, params.getPublishRequest() );
        }

        if ( params instanceof CreatePublishRequestIssueParams )
        {
            publishRequestIssueSerializer.toCreateNodeData( (CreatePublishRequestIssueParams) params, issueAsData );
        }

        return propertyTree;
    }

    public PropertyTree toUpdateNodeData( final Issue editedIssue )
    {
        final PropertyTree propertyTree = new PropertyTree();

        final PropertySet issueAsData = propertyTree.getRoot();

        issueAsData.ifNotNull().addEnum( TYPE, editedIssue.getIssueType() );
        issueAsData.ifNotNull().addLong( INDEX, editedIssue.getIndex() );
        issueAsData.ifNotNull().addString( TITLE, editedIssue.getTitle() );
        issueAsData.ifNotNull().addInstant( CREATED_TIME, editedIssue.getCreatedTime() );
        issueAsData.ifNotNull().addInstant( MODIFIED_TIME, editedIssue.getModifiedTime() );
        issueAsData.ifNotNull().addString( CREATOR, editedIssue.getCreator().toString() );
        issueAsData.ifNotNull().addString( MODIFIER, editedIssue.getModifier().toString() );
        issueAsData.ifNotNull().addString( STATUS, editedIssue.getStatus().toString() );
        issueAsData.addString( DESCRIPTION, editedIssue.getDescription() );

        issueAsData.addStrings( APPROVERS, editedIssue.getApproverIds().
            stream().map( PrincipalKey::toString ).collect( Collectors.toList() ) );

        if ( editedIssue.getPublishRequest() != null )
        {
            addPublishRequest( issueAsData, editedIssue.getPublishRequest() );
        }

        if ( editedIssue instanceof PublishRequestIssue )
        {
            publishRequestIssueSerializer.toUpdateNodeData( (PublishRequestIssue) editedIssue, issueAsData );
        }

        return propertyTree;
    }

    public Issue.Builder fromData( final PropertySet issueProperties )
    {
        final IssueType issueType = issueProperties.getEnum( TYPE, IssueType.class );
        final Issue.Builder builder;
        if ( IssueType.PUBLISH_REQUEST == issueType )
        {
            builder = PublishRequestIssue.create();
            publishRequestIssueSerializer.fromData( issueProperties, (PublishRequestIssue.Builder) builder );
        }
        else
        {
            builder = Issue.create();
        }

        builder.title( issueProperties.getString( TITLE ) );
        builder.description( issueProperties.getString( DESCRIPTION ) );
        builder.status( IssueStatus.valueOf( issueProperties.getString( STATUS ) ) );
        builder.index( issueProperties.getLong( INDEX ) );

        extractUserInfo( issueProperties, builder );
        extractApprovers( issueProperties, builder );
        extractPublishRequest( issueProperties, builder );

        return builder;
    }

    private void addPublishRequest( final PropertySet issueProperties, final PublishRequest publishRequest )
    {
        final PropertySet publishRequestSet = issueProperties.addSet( PUBLISH_REQUEST );

        publishRequestSet.addStrings( PublishRequestPropertyNames.EXCLUDE_IDS,
                                      publishRequest.getExcludeIds().stream().map( ContentId::toString ).collect( Collectors.toList() ) );

        final Collection<PropertySet> itemSets = new ArrayList<>();
        for ( final PublishRequestItem item : publishRequest.getItems() )
        {
            final PropertySet itemSet = issueProperties.getTree().newSet();
            itemSet.setReference( PublishRequestPropertyNames.ITEM_ID, new Reference( NodeId.from( item.getId() ) ) );
            itemSet.setBoolean( PublishRequestPropertyNames.ITEM_RECURSIVE, item.getIncludeChildren() );
            itemSets.add( itemSet );
        }

        publishRequestSet.addSets( PublishRequestPropertyNames.ITEMS, itemSets.toArray( new PropertySet[0] ) );
    }

    private void extractApprovers( final PropertySet issueProperties, final Issue.Builder builder )
    {
        for ( String approver : issueProperties.getStrings( APPROVERS ) )
        {
            builder.addApproverId( PrincipalKey.from( approver ) );
        }
    }

    private void extractPublishRequest( final PropertySet issueProperties, final Issue.Builder builder )
    {
        final PropertySet publishRequestSet = issueProperties.getSet( PUBLISH_REQUEST );

        if ( publishRequestSet == null )
        {
            return;
        }

        final PublishRequest.Builder publishRequestBuilder = PublishRequest.create();

        publishRequestBuilder.addExcludeIds(
            ContentIds.from( Lists.newArrayList( publishRequestSet.getStrings( PublishRequestPropertyNames.EXCLUDE_IDS ) ) ) );

        final Iterable<PropertySet> itemSets = publishRequestSet.getSets( PublishRequestPropertyNames.ITEMS );

        for ( final PropertySet itemSet : itemSets )
        {
            publishRequestBuilder.addItem( PublishRequestItem.create().
                id( ContentId.from( itemSet.getReference( PublishRequestPropertyNames.ITEM_ID ) ) ).
                includeChildren( itemSet.getBoolean( PublishRequestPropertyNames.ITEM_RECURSIVE ) ).
                build() );
        }

        builder.setPublishRequest( publishRequestBuilder.build() );
    }

    private void extractUserInfo( final PropertySet issueProperties, final Issue.Builder builder )
    {
        builder.creator( PrincipalKey.from( issueProperties.getString( CREATOR ) ) );
        builder.createdTime( issueProperties.getInstant( CREATED_TIME ) );
        builder.modifier(
            issueProperties.getString( MODIFIER ) != null ? PrincipalKey.from( issueProperties.getString( MODIFIER ) ) : null );
        builder.modifiedTime( issueProperties.getInstant( MODIFIED_TIME ) );
    }
}
