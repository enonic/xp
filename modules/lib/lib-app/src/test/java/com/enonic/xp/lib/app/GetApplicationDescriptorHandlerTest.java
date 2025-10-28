package com.enonic.xp.lib.app;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationDescriptor;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.icon.Icon;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

class GetApplicationDescriptorHandlerTest
    extends BaseAppHandlerTest
{
    @Test
    void testExample()
    {

        when( applicationDescriptorService.get( isA( ApplicationKey.class ) ) ).thenAnswer( params -> {
            final ApplicationKey applicationKey = params.getArgument( 0, ApplicationKey.class );

            return ApplicationDescriptor.create()
                .key( applicationKey )
                .description( "my app description" )
                .icon( Icon.from( new byte[]{0, 1, 3}, "image/png", Instant.parse( "2021-12-03T10:15:30.00Z" ) ) )
                .build();
        } );

        runScript( "/lib/xp/examples/app/getDescriptor.js" );
    }

    @Test
    void testWithoutIcon()
    {

        when( applicationDescriptorService.get( isA( ApplicationKey.class ) ) ).thenAnswer( params -> {
            final ApplicationKey applicationKey = params.getArgument( 0, ApplicationKey.class );

            return ApplicationDescriptor.create().key( applicationKey ).description( "my app description" ).build();
        } );

        runFunction( "/test/GetApplicationDescriptorHandlerTest.js", "getWithoutIcon" );
    }

    @Test
    void testMissing()
    {

        when( applicationDescriptorService.get( isA( ApplicationKey.class ) ) ).thenAnswer( params -> null );

        runFunction( "/test/GetApplicationDescriptorHandlerTest.js", "getMissing" );
    }
}
