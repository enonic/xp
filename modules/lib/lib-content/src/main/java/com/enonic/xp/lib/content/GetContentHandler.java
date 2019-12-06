package com.enonic.xp.lib.content;

import org.apache.commons.lang.StringUtils;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentVersionId;
import com.enonic.xp.lib.content.mapper.ContentMapper;

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
            return StringUtils.isNotBlank( this.versionId )
                ? getByPathAndVersionId( ContentPath.from( this.key ), ContentVersionId.from( this.versionId ) )
                : getByPath( ContentPath.from( this.key ) );
        }
        else
        {
            return StringUtils.isNotBlank( this.versionId )
                ? getByIdAndVersionId( ContentId.from( this.key ), ContentVersionId.from( this.versionId ) )
                : getById( ContentId.from( this.key ) );
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

    private ContentMapper getByPathAndVersionId( final ContentPath path, final ContentVersionId versionId )
    {
        try
        {
            return convert( contentService.getByPathAndVersionId( path, versionId ) );
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
