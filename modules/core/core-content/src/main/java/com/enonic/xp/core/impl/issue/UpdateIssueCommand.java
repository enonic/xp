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
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.security.User;

public class UpdateIssueCommand
    extends AbstractIssueCommand
{
    private final UpdateIssueParams params;

    private UpdateIssueCommand( Builder builder )
    {
        super( builder );
        this.params = builder.params;
    }

    public Issue execute()
    {
        return doExecute();
    }

    private Issue doExecute()
    {
        Issue editedIssue = editIssue( params.getEditor(), getIssue( params ) );

        editedIssue = setModifiedTime( editedIssue );
        editedIssue = setModifier( editedIssue );

        final UpdateNodeParams updateNodeParams = UpdateNodeParamsFactory.create( editedIssue );
        final Node updatedNode = this.nodeService.update( updateNodeParams );

        nodeService.refresh( RefreshMode.SEARCH );
        return IssueNodeTranslator.fromNode( updatedNode );
    }

    private Issue editIssue( final IssueEditor editor, final Issue original )
    {
        final EditableIssue editableIssue = new EditableIssue( original );
        if ( editor != null )
        {
            editor.edit( editableIssue );
        }
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

    private Issue setModifiedTime( final Issue issue )
    {
        return Issue.create( issue ).
            modifiedTime( Instant.now() ).
            build();
    }

    private Issue setModifier( final Issue issue )
    {
        return Issue.create( issue ).
            modifier( getCurrentUser().getKey() ).
            build();
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
        extends AbstractIssueCommand.Builder<Builder>
    {

        private UpdateIssueParams params;

        public Builder params( final UpdateIssueParams params )
        {
            this.params = params;
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
