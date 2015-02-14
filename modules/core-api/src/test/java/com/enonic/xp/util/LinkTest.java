package com.enonic.xp.util;

import org.junit.Test;

import com.enonic.xp.util.Link;

import static org.junit.Assert.*;

public class LinkTest
{
    @Test
    public void relative_path()
        throws Exception
    {
        final Link from = Link.from( "./child/image" );
        assertEquals( "./child/image", from.getPath() );
    }
}