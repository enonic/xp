package com.enonic.wem.api.util;

import org.junit.Test;

import com.enonic.wem.api.support.PathUtils;

import static org.junit.Assert.*;

public class PathUtilsTest
{
    @Test
    public void join_normalized_paths()
        throws Exception
    {
        final String joinedPaths = PathUtils.getJoinedPaths( "\\", "/", "\\This\\is\\a\\test", "\\of\\path\\stuff" );

        assertEquals( "/This/is/a/test/of/path/stuff", joinedPaths );
    }

}
