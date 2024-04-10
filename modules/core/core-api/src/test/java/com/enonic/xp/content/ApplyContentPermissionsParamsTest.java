package com.enonic.xp.content;

import org.junit.jupiter.api.Test;

import com.enonic.xp.node.ApplyPermissionsMode;
import com.enonic.xp.security.acl.AccessControlList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ApplyContentPermissionsParamsTest
{
    @Test
    public void testCreate()
    {
        final ApplyContentPermissionsParams params = ApplyContentPermissionsParams.create()
            .contentId( ContentId.from( "id1" ) ).applyPermissionsMode( ApplyPermissionsMode.TREE )
            .permissions( AccessControlList.create().build() )
            .applyContentPermissionsListener( new ApplyPermissionsListener()
            {
                @Override
                public void setTotal( final int count )
                {

                }

                @Override
                public void permissionsApplied( final int count )
                {

                }

                @Override
                public void notEnoughRights( final int count )
                {

                }
            } )
            .build();

        assertEquals( ContentId.from( "id1" ), params.getContentId() );
        assertEquals( ApplyPermissionsMode.TREE, params.getMode() );
        assertEquals( AccessControlList.create().build(), params.getPermissions() );
        assertNotNull( params.getListener() );
    }
}
