package com.enonic.xp.core.impl.content;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.EditableContentWorkflow;
import com.enonic.xp.content.UpdateWorkflowParams;
import com.enonic.xp.content.UpdateWorkflowResult;
import com.enonic.xp.content.WorkflowEditor;
import com.enonic.xp.context.ContextAccessor;
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
        final PatchNodeParams patchNodeParams = PatchNodeParamsFactory.create()
            .contentId( params.getContentId() )
            .editor( content -> {
                Content editedContent = editWorkflow( params.getEditor(), content );
                return afterUpdate( editedContent );
            } )
            .versionAttributes( ContentAttributesHelper.versionHistoryAttr( ContentAttributesHelper.UPDATE_WORKFLOW_ATTR ) )
            .contentTypeService( this.contentTypeService )
            .mixinService( this.mixinService )
            .pageDescriptorService( this.pageDescriptorService )
            .partDescriptorService( this.partDescriptorService )
            .layoutDescriptorService( this.layoutDescriptorService )
            .cmsService( this.cmsService )
            .build()
            .produce();

        final PatchNodeResult result = nodeService.patch( patchNodeParams );

        return UpdateWorkflowResult.create()
            .content( ContentNodeTranslator.fromNode( result.getResult( ContextAccessor.current().getBranch() ) ) )
            .build();
    }

    private Content editWorkflow( final WorkflowEditor editor, final Content original )
    {
        final EditableContentWorkflow editableWorkflow = new EditableContentWorkflow( original );
        if ( editor != null )
        {
            editor.edit( editableWorkflow );
        }

        return editableWorkflow.build();
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
