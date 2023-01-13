package com.enonic.xp.core.impl.issue;

import com.enonic.xp.core.impl.issue.serializer.IssueCommentDataSerializer;
import com.enonic.xp.issue.IssueComment;
import com.enonic.xp.issue.UpdateIssueCommentParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.node.UpdateNodeParams;

public class UpdateIssueCommentCommand
    extends AbstractIssueCommand
{
    private static final IssueCommentDataSerializer ISSUE_COMMENT_DATA_SERIALIZER = new IssueCommentDataSerializer();

    private final UpdateIssueCommentParams params;

    private UpdateIssueCommentCommand( Builder builder )
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

        final UpdateNodeParams updateNodeParams = UpdateNodeParams.create()
            .id( this.params.getComment() )
            .editor( e -> ISSUE_COMMENT_DATA_SERIALIZER.updateNodeData( e.data, this.params ) )
            .refresh( RefreshMode.SEARCH )
            .build();

        final Node udpatedNode = nodeService.update( updateNodeParams );

        return IssueCommentNodeTranslator.fromNode( udpatedNode );
    }

    private void validateBlockingChecks()
    {
        if ( params.getComment() == null )
        {
            throw new IllegalArgumentException( "Issue comment id can not be null." );
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

        private UpdateIssueCommentParams params;

        public Builder params( final UpdateIssueCommentParams params )
        {
            this.params = params;
            return this;
        }

        private Builder()
        {
        }

        public UpdateIssueCommentCommand build()
        {
            return new UpdateIssueCommentCommand( this );
        }
    }
}
