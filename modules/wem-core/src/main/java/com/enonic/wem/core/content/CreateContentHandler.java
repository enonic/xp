package com.enonic.wem.core.content;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.content.CreateContent;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.dao.ContentDao;

import com.enonic.cms.core.time.TimeService;

@Component
public class CreateContentHandler
    extends CommandHandler<CreateContent>
{
    private ContentDao contentDao;

    private TimeService timeService;

    public CreateContentHandler()
    {
        super( CreateContent.class );
    }

    @Override
    public void handle( final CommandContext context, final CreateContent command )
        throws Exception
    {
        final ContentData contentData = command.getContentData();
        final Content content = new Content();
        content.setPath( command.getContentPath() );
        content.setData( contentData );
        content.setType( command.getContentType() );
        content.setDisplayName( command.getDisplayName() );
        content.setCreatedTime( timeService.getNowAsDateTime() );
        content.setModifiedTime( timeService.getNowAsDateTime() );
        content.setOwner( command.getOwner() );
        content.setModifier( command.getOwner() );
        contentDao.createContent( content, context.getJcrSession() );
        context.getJcrSession().save();
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
