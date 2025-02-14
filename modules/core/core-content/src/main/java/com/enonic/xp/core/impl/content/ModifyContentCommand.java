package com.enonic.xp.core.impl.content;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentSuperEditor;
import com.enonic.xp.content.ModifyContentParams;
import com.enonic.xp.content.ModifyContentResult;
import com.enonic.xp.content.SuperEditableContent;
import com.enonic.xp.content.SuperEditableSite;
import com.enonic.xp.node.ModifyNodeResult;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.site.Site;

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

    public ModifyContentResult execute()
    {
        final Content contentBeforeChange = getContent( params.getContentId() );

        Content editedContent = superEditContent( params.getEditor(), contentBeforeChange );

        final UpdateNodeParams updateNodeParams = UpdateNodeParamsFactory.create()
            .editedContent( editedContent )
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

        final ModifyNodeResult result = nodeService.modify( updateNodeParams );

        final ModifyContentResult.Builder builder = ModifyContentResult.create().contentId( ContentId.from( result.getNodeId() ) );

        result.getResults()
            .forEach( branchResult -> builder.addResult( branchResult.branch(), translator.fromNode( branchResult.node(), true ) ) );

        return builder.build();
    }

    private Content superEditContent( final ContentSuperEditor editor, final Content original )
    {
        final SuperEditableContent editableContent =
            original.isSite() ? new SuperEditableSite( (Site) original ) : new SuperEditableContent( original );
        if ( editor != null )
        {
            editor.edit( editableContent );
        }
        return editableContent.build();
    }

    public static final class Builder
        extends AbstractCreatingOrUpdatingContentCommand.Builder<Builder>
    {
        private ModifyContentParams params;

        private Builder()
        {
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
