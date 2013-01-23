package com.enonic.wem.core.content;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.content.GetContents;
import com.enonic.wem.api.content.ContentSelectors;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.dao.ContentDao;

@Component
public class GetContentsHandler
    extends CommandHandler<GetContents>
{
    private ContentDao contentDao;

    public GetContentsHandler()
    {
        super( GetContents.class );
    }

    @Override
    public void handle( final CommandContext context, final GetContents command )
        throws Exception
    {
        final ContentSelectors selectors = command.getSelectors();
        final Contents result = contentDao.select( selectors, context.getJcrSession() );
        command.setResult( result );
    }

    @Autowired
    public void setContentDao( final ContentDao contentDao )
    {
        this.contentDao = contentDao;
    }
}
