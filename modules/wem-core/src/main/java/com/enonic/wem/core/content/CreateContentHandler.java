package com.enonic.wem.core.content;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.content.CreateContent;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;

@Component
public class CreateContentHandler
    extends CommandHandler<CreateContent>
{
    private MockContentDao contentDao = MockContentDao.get();

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
        contentDao.store( content );
        System.out.println( "Content created" );
    }

}
