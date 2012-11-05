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

import com.enonic.cms.core.time.TimeService;

@Component
public class UpdateContentsHandler
    extends CommandHandler<UpdateContents>
{
    private ContentDao contentDao;

    private TimeService timeService;

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
            if ( contentEditor.edit( contentToUpdate ) )
            {
                contentToUpdate.setModifiedTime( timeService.getNowAsDateTime() );
                contentToUpdate.setModifier( command.getModifier() );
                contentDao.updateContent( contentToUpdate, context.getJcrSession() );
                context.getJcrSession().save();
            }
        }
    }

    @Autowired
    public void setContentDao( final ContentDao contentDao )
    {
        this.contentDao = contentDao;
    }

    @Autowired
    public void setTimeService( final TimeService timeService )
    {
        this.timeService = timeService;
    }
}
