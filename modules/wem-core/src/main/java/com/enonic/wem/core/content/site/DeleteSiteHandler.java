package com.enonic.wem.core.content.site;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.UpdateContent;
import com.enonic.wem.api.command.content.site.DeleteSite;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.editor.ContentEditor;
import com.enonic.wem.core.command.CommandHandler;

import static com.enonic.wem.api.content.Content.editContent;

public class DeleteSiteHandler
    extends CommandHandler<DeleteSite>
{
    @Override
    public void handle()
        throws Exception
    {
        UpdateContent updateContent = Commands.content().update();
        updateContent.selector( command.getContent() );
        updateContent.editor( new ContentEditor()
        {
            @Override
            public Content.EditBuilder edit( final Content toBeEdited )
            {
                return editContent( toBeEdited ).site( null );
            }
        } );
        context.getClient().execute( updateContent );
    }
}
