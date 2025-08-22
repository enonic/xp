package com.enonic.xp.content;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DuplicateContentParamsTest
{
    @Test
    void testValidateDuplicateContentParams()
    {
        DuplicateContentParams.create()
            .contentId( ContentId.from( "contentId" ) )
            .includeChildren( true )
            .variant( false )
            .name( "name" )
            .parent( ContentPath.ROOT )
            .build();
        DuplicateContentParams.create()
            .contentId( ContentId.from( "contentId" ) )
            .includeChildren( false )
            .variant( true )
            .name( "name" )
            .parent( ContentPath.ROOT )
            .build();

        NullPointerException ex = assertThrows( NullPointerException.class, () -> DuplicateContentParams.create()
            .contentId( null )
            .includeChildren( true )
            .variant( true )
            .name( "name" )
            .parent( ContentPath.ROOT )
            .build() );

        assertEquals( "contentId is required", ex.getMessage() );
    }
}