package com.enonic.xp.content;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RenameContentParamsTest
{

    private final ContentId contentId = ContentId.from( "a" );

    private final ContentName contentName = ContentName.from( "name" );

    @Test
    public void testEquals()
    {
        RenameContentParams params = RenameContentParams.create().
            contentId( contentId ).
            newName( contentName ).
            build();

        assertEquals( params, params );
        assertEquals( params.getContentId(), contentId );
        assertEquals( params.getNewName(), contentName );

        assertNotEquals( params, null );

        RenameContentParams params2 = RenameContentParams.create().
            contentId( params.getContentId() ).
            newName( params.getNewName() ).
            build();

        assertEquals( params, params2 );
        assertEquals( params.getContentId(), params2.getContentId() );
        assertEquals( params.getNewName(), params2.getNewName() );
        assertEquals( params.hashCode(), params2.hashCode() );


    }

    @Test
    public void testValidateWithNullId()
    {

        RenameContentParams params = RenameContentParams.create().
            contentId( null ).
            newName( contentName ).
            build();

        assertThrows(NullPointerException.class, () ->  params.validate() );
    }

    @Test
    public void testValidateWithNullName()
    {

        RenameContentParams params = RenameContentParams.create().
            contentId( contentId ).
            newName( null ).
            build();

        assertThrows(NullPointerException.class, () ->  params.validate() );
    }

    @Test
    public void testValidate()
    {

        RenameContentParams params = RenameContentParams.create().
            contentId( contentId ).
            newName( contentName ).
            build();

        params.validate();
    }

}
