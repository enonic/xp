package com.enonic.xp.content;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MoveContentParamsTest
{

    @Test
    public void testEquals()
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
    public void testValidate()
    {
        assertThrows( NullPointerException.class, () -> MoveContentParams.create().build() );
    }

}
