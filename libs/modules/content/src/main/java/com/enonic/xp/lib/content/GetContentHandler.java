package com.enonic.xp.lib.content;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.lib.content.mapper.ContentMapper;

public final class GetContentHandler
    extends BaseContextHandler
{
    private String key;

    @Override
    protected Object doExecute()
    {
        if ( this.key.startsWith( "/" ) )
        {
            return getByPath( ContentPath.from( this.key ) );
        }
        else
        {
            return getById( ContentId.from( this.key ) );
        }
    }

    private ContentMapper getByPath( final ContentPath key )
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

    private ContentMapper getById( final ContentId key )
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

    private ContentMapper convert( final Content content )
    {
        return new ContentMapper( content );
    }

    public void setKey( final String key )
    {
        this.key = key;
    }
}
