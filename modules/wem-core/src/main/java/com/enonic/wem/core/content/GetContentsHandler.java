package com.enonic.wem.core.content;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.content.GetContents;
import com.enonic.wem.api.content.ContentPaths;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.dao.ContentDao;

@Component
public class GetContentsHandler
    extends CommandHandler<GetContents>
{
    @Autowired
    private ContentDao contentDao;

    public GetContentsHandler()
    {
        super( GetContents.class );
    }

    @Override
    public void handle( final CommandContext context, final GetContents command )
        throws Exception
    {
        Contents result = doGetContents( command.getPaths(), context );
        command.setResult( result );
    }

    private Contents doGetContents( final ContentPaths paths, final CommandContext context )
    {
        return contentDao.findContent( paths, context.getJcrSession() );
    }
}
