package com.enonic.wem.core.content;

import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.content.ContentIds;
import com.enonic.wem.api.content.ContentPaths;
import com.enonic.wem.api.content.ContentSelectors;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.dao.ContentDao;

public abstract class AbstractContentHandler<C extends Command>
    extends CommandHandler<C>
{

    protected ContentDao contentDao;

    protected AbstractContentHandler( final Class<C> type )
    {
        super( type );
    }

    protected Contents findContents( final ContentSelectors selectors, final CommandContext context )
    {
        final Contents contents;
        if ( selectors instanceof ContentPaths )
        {
            final ContentPaths paths = (ContentPaths) selectors;
            contents = getContentsByPath( paths, context );
        }
        else if ( selectors instanceof ContentIds )
        {
            final ContentIds contentIds = (ContentIds) selectors;
            contents = getContentsById( contentIds, context );
        }
        else
        {
            throw new IllegalArgumentException( "Unsupported content selector: " + selectors.getClass().getCanonicalName() );
        }
        return contents;
    }

    private Contents getContentsById( final ContentIds contentIds, final CommandContext context )
    {
        // TODO
        throw new IllegalArgumentException( "Unsupported content selector: " + contentIds.getClass().getCanonicalName() );
    }

    private Contents getContentsByPath( final ContentPaths paths, final CommandContext context )
    {
        return contentDao.findContents( paths, context.getJcrSession() );
    }

    @Autowired
    public void setContentDao( final ContentDao contentDao )
    {
        this.contentDao = contentDao;
    }
}
