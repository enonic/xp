package com.enonic.xp.core.impl.content;

import java.util.List;

import com.enonic.xp.branch.Branches;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.EditableWorkflow;
import com.enonic.xp.content.UpdateWorkflowParams;
import com.enonic.xp.content.UpdateWorkflowResult;
import com.enonic.xp.content.WorkflowEditor;
import com.enonic.xp.node.PatchNodeParams;
import com.enonic.xp.node.PatchNodeResult;

public class UpdateWorkflowCommand
    extends AbstractCreatingOrUpdatingContentCommand
{
    private final UpdateWorkflowParams params;

    private UpdateWorkflowCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final UpdateWorkflowParams params )
    {
        return create().params( params );
    }

    UpdateWorkflowResult execute()
    {
        return doExecute();
    }

    private UpdateWorkflowResult doExecute()
    {
        final Content contentBeforeChange = getContent( params.getContentId() );

        final Content editedContent = editWorkflow( params.getEditor(), contentBeforeChange );
        final List<String> modifiedFields = ContentAttributesHelper.modifiedFields( contentBeforeChange, editedContent );

        final PatchNodeParams patchNodeParams = PatchNodeParamsFactory.create()
            .editedContent( editedContent )
            .versionAttributes( ContentAttributesHelper.updateWorkflowHistoryAttr( modifiedFields ) )
            .branches( Branches.from( ContentConstants.BRANCH_DRAFT ) )
            .contentTypeService( this.contentTypeService )
            .xDataService( this.xDataService )
            .pageDescriptorService( this.pageDescriptorService )
            .partDescriptorService( this.partDescriptorService )
            .layoutDescriptorService( this.layoutDescriptorService )
            .siteService( this.siteService )
            .build()
            .produce();

        final PatchNodeResult result = nodeService.patch( patchNodeParams );

        return UpdateWorkflowResult.create().content( ContentNodeTranslator.fromNode( result.getResults().getFirst().node() ) ).build();
    }

    private Content editWorkflow( final WorkflowEditor editor, final Content original )
    {
        final EditableWorkflow editableWorkflow = new EditableWorkflow( original.getWorkflowInfo() );
        if ( editor != null )
        {
            editor.edit( editableWorkflow );
        }

        return Content.create( original )
            .workflowInfo( editableWorkflow.build() )
            .setInherit( stopDataInherit( original.getInherit() ) )
            .build();
    }

    public static final class Builder
        extends AbstractCreatingOrUpdatingContentCommand.Builder<Builder>
    {
        private UpdateWorkflowParams params;

        private Builder()
        {
        }

        public Builder params( final UpdateWorkflowParams params )
        {
            this.params = params;
            return this;
        }

        public UpdateWorkflowCommand build()
        {
            this.validate();
            return new UpdateWorkflowCommand( this );
        }
    }
}
