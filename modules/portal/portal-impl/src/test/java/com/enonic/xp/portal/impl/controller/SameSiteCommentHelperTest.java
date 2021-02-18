package com.enonic.xp.portal.impl.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SameSiteCommentHelperTest
{
    @Test
    void convert()
    {
        assertAll( () -> assertEquals( "__SAME_SITE_LAX__", SameSiteCommentHelper.convert( "Lax" ) ),
                   () -> assertEquals( "__SAME_SITE_STRICT__", SameSiteCommentHelper.convert( "strict" ) ),
                   () -> assertEquals( "__SAME_SITE_NONE__", SameSiteCommentHelper.convert( "NONE" ) ) );
    }

    @Test
    void empty()
    {
        assertAll( () -> assertEquals( "", SameSiteCommentHelper.convert( "" ) ),
                   () -> assertEquals( "", SameSiteCommentHelper.convert( null ) ) );
    }

    @Test
    void illegal()
    {
        assertThrows( IllegalArgumentException.class, () -> SameSiteCommentHelper.convert( "ILLEGAL" ) );
    }
}
