package com.enonic.xp.lib.app;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationMode;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

public class GetApplicationModeHandlerTest
    extends BaseAppHandlerTest
{
    @Test
    public void testExample()
    {

        when( applicationService.getApplicationMode( isA( ApplicationKey.class ) ) ).thenReturn( ApplicationMode.AUGMENTED );

        runScript( "/lib/xp/examples/app/getApplicationMode.js" );
    }
}
