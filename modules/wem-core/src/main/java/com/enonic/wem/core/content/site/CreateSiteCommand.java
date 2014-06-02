package com.enonic.wem.core.content.site;

import com.enonic.wem.api.content.ContentConstants;
import com.enonic.wem.api.content.site.CreateSiteParams;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.UpdateContentParams;
import com.enonic.wem.api.content.editor.ContentEditor;
import com.enonic.wem.api.content.site.Site;

import static com.enonic.wem.api.content.Content.editContent;

final class CreateSiteCommand
{
    private CreateSiteParams params;

    private ContentService contentService;

    public Content execute()
    {
        final Site site = Site.newSite().
            template( this.params.getTemplate() ).
            moduleConfigs( this.params.getModuleConfigs() ).
            build();

        final UpdateContentParams params = new UpdateContentParams().
            contentId( this.params.getContent() ).
            editor( new ContentEditor()
            {
                @Override
                public Content.EditBuilder edit( final Content toBeEdited )
                {
                    return editContent( toBeEdited ).site( site );
                }
            } );

        return this.contentService.update( params, ContentConstants.DEFAULT_CONTEXT);
    }

    public CreateSiteCommand params( final CreateSiteParams params )
    {
        this.params = params;
        return this;
    }

    public CreateSiteCommand contentService( final ContentService contentService )
    {
        this.contentService = contentService;
        return this;
    }
}
