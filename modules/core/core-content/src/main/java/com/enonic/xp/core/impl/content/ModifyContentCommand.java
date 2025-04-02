package com.enonic.xp.core.impl.content;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentModifier;
import com.enonic.xp.content.ModifiableContent;
import com.enonic.xp.content.ModifyContentParams;
import com.enonic.xp.content.ModifyContentResult;
import com.enonic.xp.node.PatchNodeParams;
import com.enonic.xp.node.PatchNodeResult;

public class ModifyContentCommand
    extends AbstractCreatingOrUpdatingContentCommand
{
    private final ModifyContentParams params;

    private ModifyContentCommand( final Builder builder )
    {
        super( builder );
        this.params = builder.params;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final ModifyContentParams params )
    {
        return create().params( params );
    }

    public static Builder create( final AbstractCreatingOrUpdatingContentCommand source )
    {
        return new Builder( source );
    }

    ModifyContentResult execute()
    {
        validateCreateAttachments( params.getCreateAttachments() );
        return doExecute();
    }

    private ModifyContentResult doExecute()
    {
        final Content contentBeforeChange = getContent( params.getContentId() );
        final Content modifiedContent = modifyContent( params.getModifier(), contentBeforeChange );

        final PatchNodeParams updateNodeParams = PatchNodeParamsFactory.create().editedContent( modifiedContent )
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

        final PatchNodeResult result = nodeService.patch( updateNodeParams );

        final ModifyContentResult.Builder builder = ModifyContentResult.create().contentId( ContentId.from( result.getNodeId() ) );

        result.getResults()
            .forEach( branchResult -> builder.addResult( branchResult.branch(), translator.fromNode( branchResult.node(), true ) ) );

        return builder.build();
    }

    private Content modifyContent( final ContentModifier modifier, final Content original )
    {
        final ModifiableContent modifiableContent = new ModifiableContent( original );

        if ( modifier != null )
        {
            modifier.modify( modifiableContent );
        }

        return modifiableContent.build();
    }

    public static final class Builder
        extends AbstractCreatingOrUpdatingContentCommand.Builder<Builder>
    {
        private ModifyContentParams params;

        private Builder()
        {
        }

        private Builder( final AbstractCreatingOrUpdatingContentCommand source )
        {
            super( source );
        }

        public Builder params( final ModifyContentParams params )
        {
            this.params = params;
            return this;
        }


        public ModifyContentCommand build()
        {
            this.validate();
            return new ModifyContentCommand( this );
        }
    }
}
