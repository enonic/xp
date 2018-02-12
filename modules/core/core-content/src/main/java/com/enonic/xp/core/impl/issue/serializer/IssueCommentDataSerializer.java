package com.enonic.xp.core.impl.issue.serializer;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.issue.CreateIssueCommentParams;
import com.enonic.xp.issue.IssueComment;
import com.enonic.xp.issue.UpdateIssueCommentParams;
import com.enonic.xp.security.PrincipalKey;

import static com.enonic.xp.core.impl.issue.IssueCommentPropertyNames.CREATED_TIME;
import static com.enonic.xp.core.impl.issue.IssueCommentPropertyNames.CREATOR;
import static com.enonic.xp.core.impl.issue.IssueCommentPropertyNames.CREATOR_DISPLAY_NAME;
import static com.enonic.xp.core.impl.issue.IssueCommentPropertyNames.TEXT;

public class IssueCommentDataSerializer
{
    public PropertyTree toCreateNodeData( final CreateIssueCommentParams params )
    {
        final PropertyTree propertyTree = new PropertyTree();
        final PropertySet commentAsData = propertyTree.getRoot();

        commentAsData.ifNotNull().addInstant( CREATED_TIME, params.getCreated() );
        commentAsData.ifNotNull().addString( CREATOR, params.getCreator().toString() );
        commentAsData.ifNotNull().addString( CREATOR_DISPLAY_NAME, params.getCreatorDisplayName() );
        commentAsData.ifNotNull().addString( TEXT, params.getText() );

        return propertyTree;
    }

    public void updateNodeData( final PropertyTree nodeData, final UpdateIssueCommentParams params )
    {
        nodeData.setString( TEXT, params.getText() );
    }

    public IssueComment.Builder fromData( final PropertyTree commentProperties )
    {
        final IssueComment.Builder builder = IssueComment.create();

        builder.text( commentProperties.getString( TEXT ) );
        builder.creator( PrincipalKey.from( commentProperties.getString( CREATOR ) ) );
        builder.creatorDisplayName( commentProperties.getString( CREATOR_DISPLAY_NAME ) );
        builder.created( commentProperties.getInstant( CREATED_TIME ) );

        return builder;
    }

}
