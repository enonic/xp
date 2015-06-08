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

        MoveContentParams params = new MoveContentParams( contentId, parentPath );
        params.creator( PrincipalKey.ofAnonymous() );

        assertEquals( contentId, params.getContentId() );
        assertEquals( parentPath, params.getParentContentPath() );
        assertEquals( PrincipalKey.ofAnonymous(), params.getCreator() );

        MoveContentParams params2 = new MoveContentParams( params.getContentId(), params.getParentContentPath() );

        assertEquals( params, params2 );
        assertEquals( params.hashCode(), params2.hashCode() );

    }

    @Test(expected = NullPointerException.class)
    public void testValidate()
    {
        MoveContentParams invalidParams = new MoveContentParams( null, ContentPath.ROOT );
        invalidParams.validate();
    }

}
