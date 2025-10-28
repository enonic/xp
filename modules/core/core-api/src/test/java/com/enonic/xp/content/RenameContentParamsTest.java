package com.enonic.xp.content;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class RenameContentParamsTest
{

    private final ContentId contentId = ContentId.from( "a" );

    private final ContentName contentName = ContentName.from( "name" );

    @Test
    void testValidateWithNullId()
    {
        assertThrows(NullPointerException.class, () ->  RenameContentParams.create().
            contentId( null ).
            newName( contentName ).
            build() );
    }

    @Test
    void testValidateWithNullName()
    {
        assertThrows(NullPointerException.class, () ->  RenameContentParams.create().
            contentId( contentId ).
            newName( null ).
            build() );
    }
}
