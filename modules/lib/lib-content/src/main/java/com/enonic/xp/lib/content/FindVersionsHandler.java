package com.enonic.xp.lib.content;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentVersions;
import com.enonic.xp.content.FindContentVersionsParams;
import com.enonic.xp.content.FindContentVersionsResult;
import com.enonic.xp.lib.content.mapper.ContentVersionsResultMapper;

public class FindVersionsHandler
    extends BaseContextHandler
{
    private String key;

    private Integer from;

    private Integer size;

    public void setKey( final String key )
    {
        this.key = key;
    }

    public void setStart( final Integer start )
    {
        this.from = start;
    }

    public void setCount( final Integer count )
    {
        this.size = count;
    }

    @Override
    protected Object doExecute()
    {
        final ContentId contentId = getContentId();
        int from = this.from == null ? 0 : this.from;
        int size = this.size == null ? 10 : this.size;
        final FindContentVersionsResult result;
        if ( contentId == null )
        {
            result = FindContentVersionsResult.create().
                contentVersions( ContentVersions.create().build() ).
                from( from ).
                hits( 0L ).
                size( size ).
                build();
        }
        else
        {
            final FindContentVersionsParams params = FindContentVersionsParams.create().
                contentId( contentId ).
                from( from ).
                size( size ).
                build();
            result = this.contentService.getVersions( params );
        }

        return new ContentVersionsResultMapper( result );
    }

    private ContentId getContentId()
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
