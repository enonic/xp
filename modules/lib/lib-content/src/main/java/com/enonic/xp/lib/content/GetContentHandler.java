package com.enonic.xp.lib.content;


import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentVersionId;
import com.enonic.xp.lib.content.mapper.ContentMapper;

import static com.google.common.base.Strings.nullToEmpty;

public final class GetContentHandler
    extends BaseContextHandler
{
    private String key;

    private String versionId;

    @Override
    protected Object doExecute()
    {
        if ( this.key.startsWith( "/" ) )
        {
            throw new IllegalArgumentException( "Key must be id:" + this.key );
        }
        else
        {
            return nullToEmpty( this.versionId ).isBlank()
                ? getById( ContentId.from( this.key ) )
                : getByIdAndVersionId( ContentId.from( this.key ), ContentVersionId.from( this.versionId ) );
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

    private ContentMapper getByIdAndVersionId( final ContentId key, final ContentVersionId versionId )
    {
        try
        {
            return convert( contentService.getByIdAndVersionId( key, versionId ) );
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

    public void setVersionId( final String versionId )
    {
        this.versionId = versionId;
    }

}
