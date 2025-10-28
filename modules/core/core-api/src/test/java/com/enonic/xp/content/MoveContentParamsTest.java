package com.enonic.xp.content;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MoveContentParamsTest
{

    @Test
    void testEquals()
    {
        final ContentId contentId = ContentId.from( "a" );

        final ContentPath parentPath = ContentPath.ROOT;

        MoveContentParams params = MoveContentParams.create().
            contentId( contentId ).
            parentContentPath( parentPath ).
            build();

        assertEquals( contentId, params.getContentId() );
        assertEquals( parentPath, params.getParentContentPath() );
    }

    @Test
    void testValidate()
    {
        assertThrows( NullPointerException.class, () -> MoveContentParams.create().build() );
    }

}
