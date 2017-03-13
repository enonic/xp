package com.enonic.xp.core.impl.issue;

import com.enonic.xp.issue.Issue;
import com.enonic.xp.issue.IssueId;
import com.enonic.xp.issue.IssueNotFoundException;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.util.Exceptions;

public class GetIssueByIdCommand
{
    private final NodeService nodeService;

    private final IssueId issueId;

    private GetIssueByIdCommand( Builder builder )
    {
        this.issueId = builder.issueId;
        this.nodeService = builder.nodeService;
    }

    public Issue execute()
    {
        final NodeId nodeId = NodeId.from( issueId.toString() );

        try
        {
            final Node node = nodeService.getById( nodeId );
            return IssueNodeTranslator.fromNode( node );
        }
        catch ( NodeNotFoundException e )
        {
            throw new IssueNotFoundException( issueId );
        }
        catch ( Exception e )
        {
            throw Exceptions.newRuntime( "Error getting node" ).withCause( e );
        }
    }

    public static Builder create()
    {
        return new Builder();
    }

    static class Builder
    {

        private IssueId issueId;

        private NodeService nodeService;

        private Builder()
        {
        }

        public Builder issueId( final IssueId issueId )
        {
            this.issueId = issueId;
            return this;
        }

        public Builder nodeService( final NodeService nodeService )
        {
            this.nodeService = nodeService;
            return this;
        }

        public GetIssueByIdCommand build()
        {
            return new GetIssueByIdCommand( this );
        }
    }
}
