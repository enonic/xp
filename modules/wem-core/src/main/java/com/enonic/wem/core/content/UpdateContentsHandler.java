package com.enonic.wem.core.content;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.content.UpdateContents;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.content.editor.ContentEditor;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.dao.ContentDao;

@Component
public class UpdateContentsHandler
    extends CommandHandler<UpdateContents>
{
    @Autowired
    private ContentDao contentDao;

    private MockContentTypeDao contentTypeDao = MockContentTypeDao.get();

    public UpdateContentsHandler()
    {
        super( UpdateContents.class );
    }

    @Override
    public void handle( final CommandContext context, final UpdateContents command )
        throws Exception
    {
        final Contents contents = contentDao.findContent( command.getPaths(), context.getJcrSession() );
        for ( Content contentToUpdate : contents )
        {
            ContentEditor contentEditor = command.getEditor();
            contentEditor.edit( contentToUpdate );
            contentDao.updateContent( contentToUpdate, context.getJcrSession() );
            context.getJcrSession().save();
        }
    }
}
