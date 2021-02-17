package com.enonic.xp.core.internal;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HtmlHelperTest
{

    @Test
    void unescape()
    {
        assertEquals( " ", HtmlHelper.unescape( " " ) );
        assertEquals( "<>", HtmlHelper.unescape( "&lt;&gt;" ) );
    }
}
