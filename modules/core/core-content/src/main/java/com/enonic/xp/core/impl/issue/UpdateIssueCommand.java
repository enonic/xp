package com.enonic.xp.core.impl.issue;

import java.time.Instant;
import java.util.Objects;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.core.impl.issue.serializer.IssueDataSerializer;
import com.enonic.xp.issue.EditableIssue;
import com.enonic.xp.issue.EditablePublishRequestIssue;
import com.enonic.xp.issue.Issue;
import com.enonic.xp.issue.IssueEditor;
import com.enonic.xp.issue.IssueId;
import com.enonic.xp.issue.PublishRequestIssue;
import com.enonic.xp.issue.UpdateIssueParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.security.User;

public class UpdateIssueCommand
    extends AbstractIssueCommand
{
    private static final IssueDataSerializer ISSUE_DATA_SERIALIZER = new IssueDataSerializer();

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
        Issue editedIssue = editIssue( params.getEditor(), getIssue( params.getId() ) );

        final UpdateNodeParams updateNodeParams = UpdateNodeParams.create()
            .id( NodeId.from( editedIssue.getId() ) )
            .editor( editableNode -> editableNode.data = ISSUE_DATA_SERIALIZER.toUpdateNodeData( editedIssue ) )
            .refresh( RefreshMode.ALL )
            .build();
        final Node updatedNode = this.nodeService.update( updateNodeParams );

        return IssueNodeTranslator.fromNode( updatedNode );
    }

    private Issue editIssue( final IssueEditor editor, final Issue original )
    {
        final EditableIssue editableIssue = original instanceof PublishRequestIssue
            ? new EditablePublishRequestIssue( (PublishRequestIssue) original )
            : new EditableIssue( original );
        if ( editor != null )
        {
            editor.edit( editableIssue );
        }

        return editableIssue.builder().modifiedTime( Instant.now() ).modifier( getCurrentUser().getKey() ).build();
    }

    private Issue getIssue( final IssueId issueId )
    {
        return GetIssueByIdCommand.create().issueId( issueId ).nodeService( this.nodeService ).build().execute();
    }

    User getCurrentUser()
    {
        return Objects.requireNonNullElseGet( ContextAccessor.current().getAuthInfo().getUser(), User::anonymous );
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
}
