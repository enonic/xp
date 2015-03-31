package com.enonic.xp.admin.impl.rest.resource.content;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPaths;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.FindContentByParentParams;
import com.enonic.xp.content.FindContentByParentResult;
import com.enonic.xp.index.ChildOrder;


public class ContentChildrenCounter
{
    private ContentService contentService;

    private static int PAGINATION_STEP = 10;

    public ContentChildrenCounter( ContentService contentService )
    {
        this.contentService = contentService;
    }

    public long countItemsAndTheirChildren( ContentPaths contentsPaths )
    {
        long totalChildren = contentsPaths.getSize();

        for(ContentPath contentPath : contentsPaths)
        {
            totalChildren += countChildren( contentPath );
        }

        return totalChildren;
    }

    public long countChildren( final ContentPath contentPath ) {

        long totalChildren = 0;
        long processedChildren = 0;
        int startPos = 0;
        FindContentByParentResult result;

        do
        {
            final FindContentByParentParams params = FindContentByParentParams.create().
                from( startPos ).
                size( PAGINATION_STEP ).
                parentPath( contentPath ).
                childOrder( ChildOrder.from( "" ) ).
                build();

            result = contentService.findByParent( params );

            totalChildren += result.getHits();
            totalChildren += countChildren( result.getContents());
            processedChildren += result.getHits();
            startPos += PAGINATION_STEP;
        }
        while( processedChildren < result.getTotalHits() );

        return totalChildren;
    }

    private long countChildren(final Contents contents) {
        long total = 0;

        for(Content content : contents.getSet())
        {
            if( content.hasChildren() )
                total += countChildren( content.getPath() );
        }

        return total;
    }
}
