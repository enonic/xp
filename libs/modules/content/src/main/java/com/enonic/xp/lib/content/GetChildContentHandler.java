package com.enonic.xp.lib.content;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.FindContentByParentParams;
import com.enonic.xp.content.FindContentByParentResult;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.lib.content.mapper.ContentsResultMapper;

public final class GetChildContentHandler
    extends BaseContextHandler
{
    static final int DEFAULT_COUNT = 10;

    private final ContentService contentService;

    private String key;

    private Integer start;

    private Integer count;

    private String sort;

    public GetChildContentHandler( final ContentService contentService )
    {
        this.contentService = contentService;
    }

    @Override
    protected Object doExecute()
    {
        final String key = checkRequired( "key", this.key );
        final int start = valueOrDefault( this.start, 0 );
        final int count = valueOrDefault( this.count, DEFAULT_COUNT );
        final String sortExpr = valueOrDefault( this.sort, "" );

        final FindContentByParentParams.Builder findContentByParent = FindContentByParentParams.create();

        if ( key.startsWith( "/" ) )
        {
            findContentByParent.parentPath( ContentPath.from( key ) );
        }
        else
        {
            findContentByParent.parentId( ContentId.from( key ) );
        }

        return getChildren( findContentByParent, start, count, sortExpr );
    }

    private ContentsResultMapper getChildren( final FindContentByParentParams.Builder findContentByParent, final int start, final int count,
                                              final String sortExpr )
    {
        final ChildOrder childOrder = ChildOrder.from( sortExpr );

        try
        {
            final FindContentByParentResult result = this.contentService.findByParent( findContentByParent.
                from( start ).
                size( count ).
                childOrder( childOrder ).build() );
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
