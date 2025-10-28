package com.enonic.xp.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LinkTest
{
    @Test
    void relative_path()
    {
        final Link from = Link.from( "./child/image" );
        assertEquals( "./child/image", from.getPath() );
    }
}
