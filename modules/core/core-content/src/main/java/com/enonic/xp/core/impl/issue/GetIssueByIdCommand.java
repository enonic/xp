package com.enonic.xp.core.impl.issue;

import com.enonic.xp.issue.Issue;
import com.enonic.xp.issue.IssueId;
import com.enonic.xp.issue.IssueNotFoundException;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeNotFoundException;

public class GetIssueByIdCommand
    extends AbstractIssueCommand
{

    private final IssueId issueId;

    private GetIssueByIdCommand( Builder builder )
    {
        super( builder );
        this.issueId = builder.issueId;
    }

    public Issue execute()
    {
        final NodeId nodeId = NodeId.from( issueId);

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
            throw new RuntimeException( "Error getting node", e );
        }
    }

    public static Builder create()
    {
        return new Builder();
    }

    static class Builder
        extends AbstractIssueCommand.Builder<Builder>
    {

        private IssueId issueId;

        private Builder()
        {
        }

        public Builder issueId( final IssueId issueId )
        {
            this.issueId = issueId;
            return this;
        }

        public GetIssueByIdCommand build()
        {
            return new GetIssueByIdCommand( this );
        }
    }
}
