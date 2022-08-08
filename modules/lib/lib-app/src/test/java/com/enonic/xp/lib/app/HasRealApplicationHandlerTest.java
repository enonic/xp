package com.enonic.xp.lib.app;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

public class HasRealApplicationHandlerTest
    extends BaseAppHandlerTest
{
    @Test
    public void testExample()
    {

        when( applicationService.hasReal( isA( ApplicationKey.class ) ) ).thenReturn( true );

        runScript( "/lib/xp/examples/app/hasReal.js" );
    }
}
