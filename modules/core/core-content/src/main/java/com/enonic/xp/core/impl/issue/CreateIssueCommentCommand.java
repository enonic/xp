package com.enonic.xp.core.impl.issue;

import com.enonic.xp.core.impl.issue.serializer.IssueCommentDataSerializer;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.issue.CreateIssueCommentParams;
import com.enonic.xp.issue.IssueAlreadyExistsException;
import com.enonic.xp.issue.IssueComment;
import com.enonic.xp.issue.IssueName;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeAlreadyExistAtPathException;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.RefreshMode;

public class CreateIssueCommentCommand
    extends AbstractIssueCommand
{
    private final CreateIssueCommentParams params;

    private CreateIssueCommentCommand( Builder builder )
    {
        super( builder );
        this.params = builder.params;
    }

    public IssueComment execute()
    {
        return doExecute();
    }

    private IssueComment doExecute()
    {
        validateBlockingChecks();
        final Node issueNode = nodeService.getById( NodeId.from( params.getIssue() ) );

        final String commentName = IssueCommentNameFactory.create( params.getCreated() );

        final CreateNodeParams createNodeParams = CreateNodeParamsFactory.create( this.params, issueNode.name(), commentName );

        final Node createdNode;
        try
        {
            createdNode = nodeService.create( createNodeParams );
        }
        catch ( NodeAlreadyExistAtPathException e )
        {
            throw new IssueAlreadyExistsException( IssueName.from( createNodeParams.getName() ) );
        }

        nodeService.refresh( RefreshMode.SEARCH );
        return IssueCommentNodeTranslator.fromNode( createdNode );
    }

    private void validateBlockingChecks()
    {
        if ( params.getIssue() == null )
        {
            throw new IllegalArgumentException( "Issue id can not be null." );
        }
        if ( params.getText() == null )
        {
            throw new IllegalArgumentException( "Issue comment text can not be null." );
        }
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
        extends AbstractIssueCommand.Builder<Builder>
    {

        private CreateIssueCommentParams params;

        public Builder params( final CreateIssueCommentParams params )
        {
            this.params = params;
            return this;
        }

        private Builder()
        {
        }

        public CreateIssueCommentCommand build()
        {
            return new CreateIssueCommentCommand( this );
        }
    }

    private static class CreateNodeParamsFactory
    {

        private static final IssueCommentDataSerializer ISSUE_COMMENT_DATA_SERIALIZER = new IssueCommentDataSerializer();

        public static CreateNodeParams create( final CreateIssueCommentParams params, NodeName parentName, String commentName )
        {
            final PropertyTree commentAsData = ISSUE_COMMENT_DATA_SERIALIZER.toCreateNodeData( params );

            final CreateNodeParams.Builder builder = CreateNodeParams.create().
                name( commentName ).
                parent( NodePath.create( IssueConstants.ISSUE_ROOT_PATH, parentName.toString() ).build() ).
                data( commentAsData ).
                inheritPermissions( true ).
                childOrder( IssueCommentConstants.DEFAULT_CHILD_ORDER ).
                nodeType( IssueCommentConstants.NODE_COLLECTION );

            return builder.build();
        }
    }
}
