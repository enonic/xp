package com.enonic.wem.core.content;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.content.GetContents;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;

@Component
public class GetContentsHandler
    extends CommandHandler<GetContents>
{
    private MockContentDao contentDao = MockContentDao.get();

    public GetContentsHandler()
    {
        super( GetContents.class );
    }

    @Override
    public void handle( final CommandContext context, final GetContents command )
        throws Exception
    {
        contentDao.getContentByPaths( command.getPaths() );
    }
}
