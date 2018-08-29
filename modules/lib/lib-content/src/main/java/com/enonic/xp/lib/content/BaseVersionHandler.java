package com.enonic.xp.lib.content;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;

public abstract class BaseVersionHandler
    extends BaseContextHandler
{
    protected String key;

    public void setKey( final String key )
    {
        this.key = key;
    }

    protected ContentId getContentId()
    {
        if ( this.key.startsWith( "/" ) )
        {
            try
            {
                return this.contentService.getByPath( ContentPath.from( key ) ).
                    getId();
            }
            catch ( final ContentNotFoundException e )
            {
                return null;
            }
        }
        else
        {
            return ContentId.from( key );
        }
    }
}
