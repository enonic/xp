package com.enonic.xp.core.impl.content;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.UpdateMetadataParams;
import com.enonic.xp.content.UpdateMetadataResult;
import com.enonic.xp.node.PatchNodeParams;
import com.enonic.xp.node.PatchNodeResult;

public class UpdateMetadataCommand
    extends AbstractCreatingOrUpdatingContentCommand
{
    private final UpdateMetadataParams params;

    private UpdateMetadataCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final UpdateMetadataParams params )
    {
        return create().params( params );
    }

    UpdateMetadataResult execute()
    {
        return doExecute();
    }

    private UpdateMetadataResult doExecute()
    {
        final Content contentBeforeChange = getContent( params.getContentId() );

        final Content updatedContent = updateMetadata( contentBeforeChange );

        final PatchNodeParams patchNodeParams = PatchNodeParamsFactory.create()
            .editedContent( updatedContent )
            .versionAttributes( ContentAttributesHelper.versionHistoryAttr( ContentAttributesHelper.PATCH_ATTR ) )
            .branches( params.getBranches() )
            .contentTypeService( this.contentTypeService )
            .xDataService( this.xDataService )
            .pageDescriptorService( this.pageDescriptorService )
            .partDescriptorService( this.partDescriptorService )
            .layoutDescriptorService( this.layoutDescriptorService )
            .siteService( this.siteService )
            .build()
            .produce();

        final PatchNodeResult result = nodeService.patch( patchNodeParams );

        final UpdateMetadataResult.Builder builder = UpdateMetadataResult.create().contentId( ContentId.from( result.getNodeId() ) );

        result.getResults()
            .forEach( branchResult -> builder.addResult( branchResult.branch(), branchResult.node() != null
                ? ContentNodeTranslator.fromNode( branchResult.node() )
                : null ) );

        return builder.build();
    }

    private Content updateMetadata( final Content original )
    {
        final Content.Builder<?> builder = Content.create( original );

        if ( params.getLanguage() != null )
        {
            builder.language( params.getLanguage() );
        }

        if ( params.getOwner() != null )
        {
            builder.owner( params.getOwner() );
        }

        return builder.build();
    }

    public static final class Builder
        extends AbstractCreatingOrUpdatingContentCommand.Builder<Builder>
    {
        private UpdateMetadataParams params;

        private Builder()
        {
        }

        public Builder params( final UpdateMetadataParams params )
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
