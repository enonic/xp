package com.enonic.xp.content;

import org.junit.jupiter.api.Test;

import com.enonic.xp.security.acl.AccessControlList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ApplyContentPermissionsParamsTest
{
    @Test
    public void testCreate()
    {
        final ApplyContentPermissionsParams params = ApplyContentPermissionsParams.create()
            .contentId( ContentId.from( "id1" ) ).overwriteChildPermissions( true ).immediate( true )
            .permissions( AccessControlList.create().build() ).applyContentPermissionsListener( ApplyPermissionsListener.EMPTY )
            .build();

        assertEquals( ContentId.from( "id1" ), params.getContentId() );
        assertTrue( params.isOverwriteChildPermissions() );
        assertEquals( AccessControlList.create().build(), params.getPermissions() );
        assertTrue( params.isImmediate() );
        assertEquals( ApplyPermissionsListener.EMPTY, params.getListener() );
    }
}
