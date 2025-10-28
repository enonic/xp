package com.enonic.xp.lib.app;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

class DeleteVirtualApplicationHandlerTest
    extends BaseAppHandlerTest
{
    @Test
    void testExample()
    {
        when( applicationService.deleteVirtualApplication( isA( ApplicationKey.class ) ) ).thenReturn( true );

        runScript( "/lib/xp/examples/app/delete.js" );
    }
}
