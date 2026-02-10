package com.enonic.xp.lib.content;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.GetContentVersionsParams;
import com.enonic.xp.content.GetContentVersionsResult;
import com.enonic.xp.lib.content.mapper.ContentVersionsMapper;

public final class GetVersionsHandler
    extends BaseContextHandler
{
    private String key;

    private Integer count;

    private String cursor;

    @Override
    protected Object doExecute()
    {
        if ( key == null || key.isEmpty() )
        {
            throw new IllegalArgumentException( "Parameter 'key' is required" );
        }

        final GetContentVersionsParams.Builder paramsBuilder = GetContentVersionsParams.create().contentId( ContentId.from( key ) );

        if ( count != null )
        {
            paramsBuilder.size( count );
        }

        if ( cursor != null )
        {
            paramsBuilder.cursor( cursor );
        }

        final GetContentVersionsResult result = contentService.getVersions( paramsBuilder.build() );
        return new ContentVersionsMapper( result );
    }

    public void setKey( final String key )
    {
        this.key = key;
    }

    public void setCount( final Integer count )
    {
        this.count = count;
    }

    public void setCursor( final String cursor )
    {
        this.cursor = cursor;
    }
}
