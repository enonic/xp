package com.enonic.xp.content;

import org.junit.Test;

import com.enonic.xp.security.PrincipalKey;

import static org.junit.Assert.*;

public class MoveContentParamsTest
{

    @Test
    public void testEquals()
    {
        final ContentId contentId = ContentId.from( "a" );

        final ContentPath parentPath = ContentPath.ROOT;

        MoveContentParams params = MoveContentParams.create().
            contentId( contentId ).
            creator( PrincipalKey.ofAnonymous() ).
            parentContentPath( parentPath ).
            build();

        assertEquals( contentId, params.getContentId() );
        assertEquals( parentPath, params.getParentContentPath() );
        assertEquals( PrincipalKey.ofAnonymous(), params.getCreator() );

        MoveContentParams params2 = MoveContentParams.create().
            contentId( contentId ).
            creator( PrincipalKey.ofAnonymous() ).
            parentContentPath( parentPath ).
            build();

        assertEquals( params, params2 );
        assertEquals( params.hashCode(), params2.hashCode() );

    }

    @Test(expected = NullPointerException.class)
    public void testValidate()
    {
        MoveContentParams invalidParams = new MoveContentParams( null );
        invalidParams.validate();
    }

}
