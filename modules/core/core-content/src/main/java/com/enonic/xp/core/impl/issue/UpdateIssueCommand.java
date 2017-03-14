package com.enonic.xp.core.impl.issue;

import java.time.Instant;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.core.impl.issue.serializer.IssueDataSerializer;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.issue.EditableIssue;
import com.enonic.xp.issue.Issue;
import com.enonic.xp.issue.IssueEditor;
import com.enonic.xp.issue.UpdateIssueParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeEditor;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.security.User;

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
        final UpdateNodeParams updateNodeParams = UpdateNodeParamsFactory.create( editIssue() );
        final Node updatedNode = this.nodeService.update( updateNodeParams );

        nodeService.refresh( RefreshMode.SEARCH );
        return IssueNodeTranslator.fromNode( updatedNode );
    }

    private Issue editIssue()
    {
        final Issue issueBeforeChange = getIssue( params );
        final EditableIssue editableIssue = new EditableIssue( issueBeforeChange );
        final IssueEditor issueEditor = params.getEditor();
        if ( issueEditor != null )
        {
            issueEditor.edit( editableIssue );
        }
        editableIssue.modifier = getCurrentUser().getKey();
        editableIssue.modifiedTime = Instant.now();
        return editableIssue.build();
    }

    private Issue getIssue( final UpdateIssueParams params )
    {
        return GetIssueByIdCommand.create().
            issueId( params.getId() ).
            nodeService( this.nodeService ).
            build().
            execute();
    }

    User getCurrentUser()
    {
        final Context context = ContextAccessor.current();

        return context.getAuthInfo().getUser() != null ? context.getAuthInfo().getUser() : User.ANONYMOUS;
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
