package com.enonic.xp.lib.content;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.FindContentByParentParams;
import com.enonic.xp.content.FindContentByParentResult;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.lib.content.mapper.ContentsResultMapper;

public final class GetChildContentHandler
    extends BaseContextHandler
{
    private String key;

    private Integer start;

    private Integer count;

    private String sort;

    @Override
    protected Object doExecute()
    {
        final FindContentByParentParams.Builder findContentByParent = FindContentByParentParams.create();

        if ( key.startsWith( "/" ) )
        {
            findContentByParent.parentPath( ContentPath.from( key ) );
        }
        else
        {
            findContentByParent.parentId( ContentId.from( key ) );
        }

        return getChildren( findContentByParent );
    }

    private ContentsResultMapper getChildren( final FindContentByParentParams.Builder params )
    {
        if ( sort != null )
        {
            params.childOrder( ChildOrder.from( sort ) );
        }
        if ( start != null )
        {
            params.from( start );
        }
        if ( count != null )
        {
            params.size( count );
        }
        try
        {
            final FindContentByParentResult result = this.contentService.findByParent( params.build() );
            return convert( result );
        }
        catch ( final ContentNotFoundException e )
        {
            return new ContentsResultMapper( Contents.empty(), 0 );
        }
    }

    private ContentsResultMapper convert( final FindContentByParentResult findContentResult )
    {
        return new ContentsResultMapper( findContentResult.getContents(), findContentResult.getTotalHits() );
    }

    public void setKey( final String key )
    {
        this.key = key;
    }

    public void setStart( final Integer start )
    {
        this.start = start;
    }

    public void setCount( final Integer count )
    {
        this.count = count;
    }

    public void setSort( final String sort )
    {
        this.sort = sort;
    }
}
