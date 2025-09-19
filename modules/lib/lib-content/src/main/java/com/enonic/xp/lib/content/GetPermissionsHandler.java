package com.enonic.xp.lib.content;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.lib.content.mapper.PermissionsMapper;

public class GetPermissionsHandler
    extends BaseContextHandler
{
    private String key;


    public void setKey( final String key )
    {
        this.key = key;
    }

    @Override
    protected Object doExecute()
    {
        final Content content = getContent();
        if ( content != null )
        {
            return new PermissionsMapper( content.getPermissions() );
        }
        return null;
    }

    private Content getContent()
    {
        try
        {
            if ( this.key.startsWith( "/" ) )
            {
                return this.contentService.getByPath( ContentPath.from( key ) );
            }
            else
            {
                return this.contentService.getById( ContentId.from( key ) );
            }
        }
        catch ( final ContentNotFoundException e )
        {
            // Do nothing
        }

        return null;
    }
}
