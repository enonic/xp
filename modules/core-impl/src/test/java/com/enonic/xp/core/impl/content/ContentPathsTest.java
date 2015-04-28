package com.enonic.xp.core.impl.content;

import java.util.Set;

import org.junit.Test;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPaths;

import static org.junit.Assert.*;

public class ContentPathsTest
{

    private static final String STRING_PATH1 = "/content/string/path1";

    private static final String STRING_PATH2 = "/content/string/path2";

    private static final String STRING_PATH3 = "/content/string/path3";

    private static final ContentPath CONTENT_PATH1 = ContentPath.from( "/content/path1" );

    private static final ContentPath CONTENT_PATH2 = ContentPath.from( "/content/path2" );

    private static final ContentPath CONTENT_PATH3 = ContentPath.from( "/content/path3" );

    @Test
    public void addAndRemoveString()
        throws Exception
    {
        Set set = ImmutableSet.of( STRING_PATH2, STRING_PATH3 );
        String[] array = {STRING_PATH2, STRING_PATH3};

        ContentPaths pathsFromSet = ContentPaths.from( STRING_PATH1 ).
            add( set );
        ContentPaths pathsFromArray = ContentPaths.from( STRING_PATH1 ).
            add( array );

        assertEquals( pathsFromSet, pathsFromArray );

        pathsFromArray = pathsFromArray.remove( set );
        pathsFromSet = pathsFromSet.remove( array );

        assertEquals( pathsFromSet, pathsFromArray );
        assertEquals( pathsFromSet.hashCode(), pathsFromArray.hashCode() );
        assertEquals( pathsFromSet.toString(), pathsFromArray.toString() );
    }

    @Test
    public void addAndRemoveContentPath()
        throws Exception
    {

        Set set = ImmutableSet.of( CONTENT_PATH2, CONTENT_PATH3 );
        ContentPath[] array = {CONTENT_PATH2, CONTENT_PATH3};

        ContentPaths pathsFromSet = ContentPaths.from( CONTENT_PATH1 ).add( set );
        ContentPaths pathsFromArray = ContentPaths.from( CONTENT_PATH1 ).add( array );

        assertEquals( pathsFromSet, pathsFromArray );

        pathsFromArray = pathsFromArray.remove( set );
        pathsFromSet = pathsFromSet.remove( array );

        assertEquals( pathsFromSet, pathsFromArray );
        assertEquals( pathsFromSet.hashCode(), pathsFromArray.hashCode() );
        assertEquals( pathsFromSet.toString(), pathsFromArray.toString() );
    }

    @Test
    public void empty()
    {
        ContentPaths pathsFromSet = ContentPaths.from( CONTENT_PATH1 );
        assertTrue( pathsFromSet.isNotEmpty() );

        pathsFromSet = pathsFromSet.remove( CONTENT_PATH1 );
        assertTrue( pathsFromSet.isEmpty() );

        ContentPaths pathsEmpty = ContentPaths.empty();
        assertTrue( pathsEmpty.isEmpty() );

        assertEquals( pathsEmpty, pathsFromSet );
    }

}