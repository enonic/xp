package com.enonic.xp.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
