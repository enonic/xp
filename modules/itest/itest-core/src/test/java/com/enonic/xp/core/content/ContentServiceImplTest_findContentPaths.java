package com.enonic.xp.core.content;

import org.junit.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPaths;
import com.enonic.xp.content.ContentQuery;
import com.enonic.xp.query.parser.QueryParser;

import static org.junit.Assert.*;

@Deprecated
public class ContentServiceImplTest_findContentPaths
    extends AbstractContentServiceTest
{
    @Test
    public void empty()
        throws Exception
    {
        final ContentQuery query = ContentQuery.create().
            queryExpr( QueryParser.parse( "" ) ).
            build();

        assertEquals( ContentPaths.empty(), contentService.findContentPaths( query ) );
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
            queryExpr( QueryParser.parse( "_path = '/content" + child1.getPath().asAbsolute() + "'" ) ).
            build();

        assertEquals( ContentPaths.from( child1.getPath() ), contentService.findContentPaths( query ) );
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

        assertEquals( contentService.findContentPaths( query ),
                      ContentPaths.from( site.getPath(), child1.getPath(), child2.getPath(), child3.getPath() ) );

        query = ContentQuery.create().
            queryExpr( QueryParser.parse(
                "_path in ('/content" + child1.getPath().asAbsolute() + "', '/content" + child2.getPath().asAbsolute() + "')" ) ).
            build();

        assertEquals( contentService.findContentPaths( query ), ContentPaths.from( child1.getPath(), child2.getPath() ) );
    }
}
