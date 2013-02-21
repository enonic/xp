package com.enonic.wem.core.content;

import javax.inject.Inject;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.content.GetChildContent;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.dao.ContentDao;

@Component
public class GetChildContentHandler
    extends CommandHandler<GetChildContent>
{
    @Inject
    private ContentDao contentDao;

    public GetChildContentHandler()
    {
        super( GetChildContent.class );
    }

    @Override
    public void handle( final CommandContext context, final GetChildContent command )
        throws Exception
    {
        Contents result = doGetContents( command.getParentPath(), context );
        command.setResult( result );
    }

    private Contents doGetContents( final ContentPath parentPath, final CommandContext context )
    {
        return contentDao.findChildContent( parentPath, context.getJcrSession() );
    }
}
