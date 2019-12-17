package com.enonic.xp.content;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ApplyContentPermissionsParamsTest
{

    @Test
    public void testEquals()
    {
        ApplyContentPermissionsParams.Builder builder = ApplyContentPermissionsParams.create().
            contentId( ContentId.from( "contentId" ) ).
            overwriteChildPermissions( true );

        ApplyContentPermissionsParams params = builder.build();
        assertEquals( params.getContentId(), builder.build().getContentId() );
        assertEquals( params.isOverwriteChildPermissions(), builder.build().isOverwriteChildPermissions() );
        assertEquals( params, builder.build() );

    }

}
