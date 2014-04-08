package com.enonic.wem.core.content.site;

import javax.inject.Inject;

import com.enonic.wem.api.command.content.site.CreateSite;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.UpdateContentParams;
import com.enonic.wem.api.content.editor.ContentEditor;
import com.enonic.wem.api.content.site.Site;
import com.enonic.wem.core.command.CommandHandler;

import static com.enonic.wem.api.content.Content.editContent;

public class CreateSiteHandler
    extends CommandHandler<CreateSite>
{
    @Inject
    private ContentService contentService;

    @Override
    public void handle()
        throws Exception
    {
        final Site site = Site.newSite().
            template( command.getTemplate() ).
            moduleConfigs( command.getModuleConfigs() ).
            build();

        final UpdateContentParams params = new UpdateContentParams().
            contentId( command.getContent() ).
            editor( new ContentEditor()
            {
                @Override
                public Content.EditBuilder edit( final Content toBeEdited )
                {
                    return editContent( toBeEdited ).site( site );
                }
            } );

        final Content updatedContent = contentService.update( params );
        command.setResult( updatedContent );
    }
}
