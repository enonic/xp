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
import com.enonic.wem.core.time.TimeService;

import static com.enonic.wem.api.content.Content.newContent;

@Component
public class UpdateContentsHandler
    extends CommandHandler<UpdateContents>
{
    private ContentDao contentDao;

    private TimeService timeService;

    public UpdateContentsHandler()
    {
        super( UpdateContents.class );
    }

    @Override
    public void handle( final CommandContext context, final UpdateContents command )
        throws Exception
    {
        final Contents contents = contentDao.findContents( command.getSelectors(), context.getJcrSession() );
        for ( Content contentToUpdate : contents )
        {
            ContentEditor contentEditor = command.getEditor();
            final Content modifiedContent = contentEditor.edit( contentToUpdate );
            if ( modifiedContent != null )
            {
                contentToUpdate = newContent( modifiedContent ).
                    modifiedTime( timeService.getNowAsDateTime() ).
                    modifier( command.getModifier() ).build();
                final boolean createNewVersion = true;
                contentDao.updateContent( contentToUpdate, createNewVersion, context.getJcrSession() );
                context.getJcrSession().save();
            }
        }
    }

    @Autowired
    public void setTimeService( final TimeService timeService )
    {
        this.timeService = timeService;
    }

    @Autowired
    public void setContentDao( final ContentDao contentDao )
    {
        this.contentDao = contentDao;
    }
}
