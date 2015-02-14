package com.enonic.xp.portal.impl.jslib.content;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentService;
import com.enonic.xp.portal.impl.jslib.mapper.ContentMapper;
import com.enonic.xp.portal.script.command.CommandHandler;
import com.enonic.xp.portal.script.command.CommandRequest;

@Component(immediate = true)
public final class GetContentHandler
    implements CommandHandler
{
    private ContentService contentService;

    @Override
    public String getName()
    {
        return "content.get";
    }

    @Override
    public Object execute( final CommandRequest req )
    {
        final String key = req.param( "key" ).required().value( String.class );
        if ( key.startsWith( "/" ) )
        {
            return getByPath( ContentPath.from( key ) );
        }
        else
        {
            return getById( ContentId.from( key ) );
        }
    }

    private Object getByPath( final ContentPath key )
    {
        try
        {
            return convert( this.contentService.getByPath( key ) );
        }
        catch ( final ContentNotFoundException e )
        {
            return null;
        }
    }

    private Object getById( final ContentId key )
    {
        try
        {
            return convert( this.contentService.getById( key ) );
        }
        catch ( final ContentNotFoundException e )
        {
            return null;
        }
    }

    private Object convert( final Content content )
    {
        return new ContentMapper( content );
    }

    @Reference
    public void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
    }
}
