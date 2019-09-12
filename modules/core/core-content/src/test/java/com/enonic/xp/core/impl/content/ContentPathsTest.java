package com.enonic.xp.core.impl.content;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPaths;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ContentPathsTest
{
    private static final ContentPath CONTENT_PATH1 = ContentPath.from( "/content/path1" );

    @Test
    public void empty()
    {
        ContentPaths pathsEmpty = ContentPaths.empty();
        assertTrue( pathsEmpty.isEmpty() );
    }

    @Test
    public void notEmpty()
    {
        ContentPaths pathsFromSet = ContentPaths.from( CONTENT_PATH1 );
        assertTrue( pathsFromSet.isNotEmpty() );
    }

}
