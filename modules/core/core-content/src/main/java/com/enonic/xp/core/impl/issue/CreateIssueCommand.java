package com.enonic.xp.core.impl.issue;

import com.enonic.xp.core.impl.issue.serializer.IssueDataSerializer;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.IndexConfigDocument;
import com.enonic.xp.issue.CreateIssueParams;
import com.enonic.xp.issue.Issue;
import com.enonic.xp.issue.IssueConstants;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.RefreshMode;

public class CreateIssueCommand
{
    private final NodeService nodeService;

    private final CreateIssueParams params;

    private final IssueNodeTranslator translator;

    private CreateIssueCommand( Builder builder )
    {
        this.params = builder.params;
        this.nodeService = builder.nodeService;
        this.translator = builder.translator;
    }

    public Issue execute()
    {
        return doExecute();
    }

    private Issue doExecute()
    {
        validateBlockingChecks();

        final CreateNodeParams createNodeParams = CreateNodeParamsFactory.create( this.params );
        final Node createdNode = nodeService.create( createNodeParams );

        nodeService.refresh( RefreshMode.SEARCH );
        return translator.fromNode( createdNode );
    }

    private void validateBlockingChecks()
    {
        if ( params.getTitle() == null )
        {
            throw new IllegalArgumentException( "Issue title can not be null." );
        }
        if ( params.getCreator() == null )
        {
            throw new IllegalArgumentException( "Issue creator can not be null." );
        }
        if ( params.getStatus() == null )
        {
            throw new IllegalArgumentException( "Issue status can not be null." );
        }
    }

    public static Builder create()
    {
        return new Builder();
    }

    static class Builder
    {

        private CreateIssueParams params;

        private NodeService nodeService;

        private IssueNodeTranslator translator;

        public Builder params( final CreateIssueParams params )
        {
            this.params = params;
            return this;
        }

        public Builder nodeService( final NodeService nodeService )
        {
            this.nodeService = nodeService;
            return this;
        }

        public Builder translator( final IssueNodeTranslator translator )
        {
            this.translator = translator;
            return this;
        }

        private Builder()
        {
        }

        public CreateIssueCommand build()
        {
            return new CreateIssueCommand( this );
        }
    }

    private static class CreateNodeParamsFactory
    {

        private static final IssueDataSerializer ISSUE_DATA_SERIALIZER = new IssueDataSerializer();

        public static CreateNodeParams create( final CreateIssueParams params )
        {
            final PropertyTree contentAsData = ISSUE_DATA_SERIALIZER.toCreateNodeData( params );

            final IndexConfigDocument indexConfigDocument = IssueIndexConfigFactory.create();

            final CreateNodeParams.Builder builder = CreateNodeParams.create().
                setNodeId( NodeId.from( params.getId().toString() ) ).
                name( params.getName().toString() ).
                parent( IssueConstants.ISSUE_ROOT_PATH ).
                data( contentAsData ).
                indexConfigDocument( indexConfigDocument ).
                permissions( IssueConstants.ACCESS_CONTROL_ENTRIES ).
                childOrder( IssueConstants.DEFAULT_CHILD_ORDER ).
                nodeType( IssueConstants.ISSUE_NODE_COLLECTION );

            return builder.build();
        }
    }
}
