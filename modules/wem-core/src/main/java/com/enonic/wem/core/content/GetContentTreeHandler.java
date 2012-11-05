package com.enonic.wem.core.content;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.content.GetContentTree;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.dao.ContentDao;

@Component
public class GetContentTreeHandler
    extends CommandHandler<GetContentTree>
{
    @Autowired
    private ContentDao contentDao;

    public GetContentTreeHandler()
    {
        super( GetContentTree.class );
    }

    @Override
    public void handle( final CommandContext context, final GetContentTree command )
        throws Exception
    {
        command.setResult( contentDao.getContentTree( context.getJcrSession() ) );
    }
}
