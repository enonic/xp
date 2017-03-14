package com.enonic.xp.core.impl.issue;

import com.enonic.xp.content.ContentIds;
import com.enonic.xp.core.impl.issue.serializer.IssueDataSerializer;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.issue.Issue;
import com.enonic.xp.issue.UpdateIssueParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeEditor;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.security.PrincipalKeys;

public class UpdateIssueCommand
{
    private final NodeService nodeService;

    private final UpdateIssueParams params;

    private UpdateIssueCommand( Builder builder )
    {
        this.params = builder.params;
        this.nodeService = builder.nodeService;
    }

    public Issue execute()
    {
        return doExecute();
    }

    private Issue doExecute()
    {
        validateBlockingChecks();

        final UpdateNodeParams updateNodeParams = UpdateNodeParamsFactory.create( editIssue() );
        final Node updatedNode = this.nodeService.update( updateNodeParams );

        nodeService.refresh( RefreshMode.SEARCH );
        return IssueNodeTranslator.fromNode( updatedNode );
    }

    private Issue editIssue()
    {
        final Issue issueBeforeChange = getIssue( params );

        final Issue.Builder editedIssue = Issue.create().
            id( params.getId() ).
            createdTime( issueBeforeChange.getCreatedTime() ).
            creator( issueBeforeChange.getCreator() ).
            modifier( params.getModifier() ).
            modifiedTime( params.getModifiedTime() );

        final PrincipalKeys approverIds =
            params.getApproverIds().getSize() > 0 ? params.getApproverIds() : issueBeforeChange.getApproverIds();
        approverIds.forEach( key -> editedIssue.addApproverId( key ) );

        final ContentIds contentIds = params.getItemIds().getSize() > 0 ? params.getItemIds() : issueBeforeChange.getItemIds();
        contentIds.forEach( contentId -> editedIssue.addItemId( contentId ) );

        editedIssue.status( params.getStatus() == null ? issueBeforeChange.getStatus() : params.getStatus() );
        editedIssue.description( params.getDescription() == null ? issueBeforeChange.getDescription() : params.getDescription() );
        editedIssue.title( params.getTitle() == null ? issueBeforeChange.getTitle() : params.getTitle() );

        return editedIssue.build();
    }

    private void validateBlockingChecks()
    {
        if ( params.getModifier() == null )
        {
            throw new IllegalArgumentException( "Issue modifier can not be null." );
        }
        if ( params.getModifiedTime() == null )
        {
            throw new IllegalArgumentException( "Issue modification time can not be null." );
        }
    }

    private Issue getIssue( final UpdateIssueParams params )
    {
        return GetIssueByIdCommand.create().
            issueId( params.getId() ).
            nodeService( this.nodeService ).
            build().
            execute();
    }

    public static Builder create()
    {
        return new Builder();
    }

    static class Builder
    {

        private UpdateIssueParams params;

        private NodeService nodeService;

        public Builder params( final UpdateIssueParams params )
        {
            this.params = params;
            return this;
        }

        public Builder nodeService( final NodeService nodeService )
        {
            this.nodeService = nodeService;
            return this;
        }

        private Builder()
        {
        }

        public UpdateIssueCommand build()
        {
            return new UpdateIssueCommand( this );
        }
    }

    private static class UpdateNodeParamsFactory
    {
        private static final IssueDataSerializer ISSUE_DATA_SERIALIZER = new IssueDataSerializer();

        public static UpdateNodeParams create( final Issue editedIssue )
        {
            final NodeEditor nodeEditor = toNodeEditor( editedIssue );

            final UpdateNodeParams.Builder builder = UpdateNodeParams.create().
                id( NodeId.from( editedIssue.getId() ) ).
                editor( nodeEditor );

            return builder.build();
        }

        private static NodeEditor toNodeEditor( final Issue editedIssue )
        {
            final PropertyTree nodeData = ISSUE_DATA_SERIALIZER.toUpdateNodeData( editedIssue );
            return editableNode -> editableNode.data = nodeData;
        }
    }
}
