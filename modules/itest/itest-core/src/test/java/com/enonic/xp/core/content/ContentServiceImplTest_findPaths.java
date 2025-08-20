package com.enonic.xp.core.content;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPaths;
import com.enonic.xp.content.ContentQuery;
import com.enonic.xp.query.parser.QueryParser;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ContentServiceImplTest_findPaths
    extends AbstractContentServiceTest
{
    @Test
    public void empty()
        throws Exception
    {
        final ContentQuery query = ContentQuery.create().
            queryExpr( QueryParser.parse( "" ) ).
            build();

        assertEquals( ContentPaths.empty(), contentService.findPaths( query ).getContentPaths() );
    }

    @Test
    public void single_item()
        throws Exception
    {
        final Content site = createContent( ContentPath.ROOT, "a" );

        final Content child3 = createContent( site.getPath(), "d" );
        final Content child2 = createContent( site.getPath(), "c" );
        final Content child1 = createContent( site.getPath(), "b" );

        final ContentQuery query = ContentQuery.create().
            queryExpr( QueryParser.parse( "_path = '/content" + child1.getPath() + "'" ) ).
            build();

        assertEquals( ContentPaths.from( child1.getPath() ), contentService.findPaths( query ).getContentPaths() );
    }

    @Test
    public void multiple_items()
        throws Exception
    {
        final Content site = createContent( ContentPath.ROOT, "a" );

        final Content child3 = createContent( site.getPath(), "d" );
        final Content child2 = createContent( site.getPath(), "c" );
        final Content child1 = createContent( site.getPath(), "b" );

        ContentQuery query = ContentQuery.create().
            queryExpr( QueryParser.parse( "" ) ).
            build();

        assertEquals( ContentPaths.from( site.getPath(), child1.getPath(), child2.getPath(), child3.getPath() ),
                      contentService.findPaths( query ).getContentPaths() );

        query = ContentQuery.create().
            queryExpr( QueryParser.parse(
                "_path in ('/content" + child1.getPath() + "', '/content" + child2.getPath() + "')" ) ).
            build();

        assertEquals( ContentPaths.from( child1.getPath(), child2.getPath() ), contentService.findPaths( query ).getContentPaths() );
    }
}
