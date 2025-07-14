package com.enonic.xp.core.impl.content;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPatcher;
import com.enonic.xp.content.PatchContentParams;
import com.enonic.xp.content.PatchContentResult;
import com.enonic.xp.content.PatchableContent;
import com.enonic.xp.node.PatchNodeParams;
import com.enonic.xp.node.PatchNodeResult;

public class PatchContentCommand
    extends AbstractCreatingOrUpdatingContentCommand
{
    private final PatchContentParams params;

    private PatchContentCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final PatchContentParams params )
    {
        return create().params( params );
    }

    public static Builder create( final AbstractCreatingOrUpdatingContentCommand source )
    {
        return new Builder( source );
    }

    PatchContentResult execute()
    {
        validateCreateAttachments( params.getCreateAttachments() );
        return doExecute();
    }

    private PatchContentResult doExecute()
    {
        final Content contentBeforeChange = getContent( params.getContentId() );
        final Content patchedContent = patchContent( params.getPatcher(), contentBeforeChange );

        final PatchNodeParams patchNodeParams = PatchNodeParamsFactory.create().editedContent( patchedContent )
            .createAttachments( params.getCreateAttachments() )
            .branches( params.getBranches() )
            .contentTypeService( this.contentTypeService )
            .xDataService( this.xDataService )
            .pageDescriptorService( this.pageDescriptorService )
            .partDescriptorService( this.partDescriptorService )
            .layoutDescriptorService( this.layoutDescriptorService )
            .contentDataSerializer( this.translator.getContentDataSerializer() )
            .siteService( this.siteService )
            .build()
            .produce();

        final PatchNodeResult result = nodeService.patch( patchNodeParams );

        final PatchContentResult.Builder builder = PatchContentResult.create().contentId( ContentId.from( result.getNodeId() ) );

        result.getResults()
            .forEach( branchResult -> builder.addResult( branchResult.branch(), branchResult.node() != null
                ? translator.fromNode( branchResult.node(), true )
                : null ) );

        return builder.build();
    }

    private Content patchContent( final ContentPatcher patcher, final Content original )
    {
        final PatchableContent patchableContent = new PatchableContent( original );

        if ( patcher != null )
        {
            patcher.patch( patchableContent );
        }

        return patchableContent.build();
    }

    public static final class Builder
        extends AbstractCreatingOrUpdatingContentCommand.Builder<Builder>
    {
        private PatchContentParams params;

        private Builder()
        {
        }

        private Builder( final AbstractCreatingOrUpdatingContentCommand source )
        {
            super( source );
        }

        public Builder params( final PatchContentParams params )
        {
            this.params = params;
            return this;
        }


        public PatchContentCommand build()
        {
            this.validate();
            return new PatchContentCommand( this );
        }
    }
}
