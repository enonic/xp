package com.enonic.xp.core.impl.content;

import java.util.List;

import com.enonic.xp.branch.Branches;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentMetadataEditor;
import com.enonic.xp.content.EditableContentMetadata;
import com.enonic.xp.content.UpdateContentMetadataParams;
import com.enonic.xp.content.UpdateContentMetadataResult;
import com.enonic.xp.node.PatchNodeParams;
import com.enonic.xp.node.PatchNodeResult;

public class UpdateMetadataCommand
    extends AbstractCreatingOrUpdatingContentCommand
{
    private final UpdateContentMetadataParams params;

    private UpdateMetadataCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final UpdateContentMetadataParams params )
    {
        return create().params( params );
    }

    UpdateContentMetadataResult execute()
    {
        return doExecute();
    }

    private UpdateContentMetadataResult doExecute()
    {
        final Content contentBeforeChange = getContent( params.getContentId() );

        final Content editedContent = editMetadata( params.getEditor(), contentBeforeChange );
        final List<String> modifiedFields = ContentAttributesHelper.modifiedFields( contentBeforeChange, editedContent );

        final PatchNodeParams patchNodeParams = PatchNodeParamsFactory.create()
            .editedContent( editedContent )
            .versionAttributes( ContentAttributesHelper.updateMetadataHistoryAttr( modifiedFields ) )
            .branches( Branches.from( ContentConstants.BRANCH_DRAFT, ContentConstants.BRANCH_MASTER ) )
            .contentTypeService( this.contentTypeService )
            .xDataService( this.xDataService )
            .pageDescriptorService( this.pageDescriptorService )
            .partDescriptorService( this.partDescriptorService )
            .layoutDescriptorService( this.layoutDescriptorService )
            .siteService( this.siteService )
            .build()
            .produce();

        final PatchNodeResult result = nodeService.patch( patchNodeParams );

        return UpdateContentMetadataResult.create()
            .content( ContentNodeTranslator.fromNode( result.getResults().getFirst().node() ) )
            .build();
    }

    private Content editMetadata( final ContentMetadataEditor editor, final Content original )
    {
        final EditableContentMetadata editableMetadata = new EditableContentMetadata( original );
        if ( editor != null )
        {
            editor.edit( editableMetadata );
        }
        return editableMetadata.build();
    }

    public static final class Builder
        extends AbstractCreatingOrUpdatingContentCommand.Builder<Builder>
    {
        private UpdateContentMetadataParams params;

        private Builder()
        {
        }

        public Builder params( final UpdateContentMetadataParams params )
        {
            this.params = params;
            return this;
        }

        public UpdateMetadataCommand build()
        {
            this.validate();
            return new UpdateMetadataCommand( this );
        }
    }
}
