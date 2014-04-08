package com.enonic.wem.core.content.site;

import javax.inject.Inject;

import com.enonic.wem.api.command.content.site.UpdateSite;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.UpdateContentParams;
import com.enonic.wem.api.content.editor.ContentEditor;
import com.enonic.wem.api.content.site.Site;
import com.enonic.wem.api.content.site.SiteNotFoundException;
import com.enonic.wem.core.command.CommandHandler;

import static com.enonic.wem.api.content.Content.editContent;

public class UpdateSiteHandler
    extends CommandHandler<UpdateSite>
{
    @Inject
    private ContentService contentService;

    @Override
    public void handle()
        throws Exception
    {
        final Content content = contentService.getById( command.getContent() );

        if ( content == null )
        {
            throw new ContentNotFoundException( command.getContent() );
        }
        if ( content.getSite() == null )
        {
            throw new SiteNotFoundException( command.getContent() );
        }

        Site.SiteEditBuilder editBuilder = command.getEditor().edit( content.getSite() );

        if ( editBuilder.isChanges() )
        {
            final Site editedSite = editBuilder.build();

            final UpdateContentParams updateContent = new UpdateContentParams().
                contentId( command.getContent() ).
                editor( new ContentEditor()
                {
                    @Override
                    public Content.EditBuilder edit( final Content toBeEdited )
                    {
                        return editContent( toBeEdited ).site( editedSite );
                    }
                } );

            contentService.update( updateContent );
        }

        final Content updatedContent = contentService.getById( command.getContent() );

        command.setResult( updatedContent );
    }
}
