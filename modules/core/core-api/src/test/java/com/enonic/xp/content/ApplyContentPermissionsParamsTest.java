package com.enonic.xp.content;

import org.junit.Test;

import com.enonic.xp.security.PrincipalKey;

import static org.junit.Assert.*;

public class ApplyContentPermissionsParamsTest
{

    @Test
    public void testEquals()
    {
        ApplyContentPermissionsParams.Builder builder = ApplyContentPermissionsParams.create().
            contentId( ContentId.from( "contentId" ) ).
            overwriteChildPermissions( true ).
            modifier( PrincipalKey.ofAnonymous() );

        ApplyContentPermissionsParams params = builder.build();
        assertEquals( params.getContentId(), builder.build().getContentId() );
        assertEquals( params.getModifier(), builder.build().getModifier() );
        assertEquals( params.isOverwriteChildPermissions(), builder.build().isOverwriteChildPermissions() );
        assertEquals( params, builder.build() );

    }

}
