package com.enonic.xp.lib.app;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.Version;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationDescriptor;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.CreateVirtualApplicationParams;
import com.enonic.xp.icon.Icon;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CreateVirtualApplicationHandlerTest
    extends BaseAppHandlerTest
{
    @Test
    void testExample()
    {

        when( applicationService.createVirtualApplication( isA( CreateVirtualApplicationParams.class ) ) ).thenAnswer( params -> {
            final ApplicationKey applicationKey = params.getArgument( 0, CreateVirtualApplicationParams.class ).getKey();

            final Application application = Mockito.mock( Application.class );

            when( application.getKey() ).thenReturn( applicationKey );
            when( application.getDisplayName() ).thenReturn( "app display name" );
            when( application.getBundle() ).thenReturn( mock( Bundle.class ) );
            when( application.getVendorName() ).thenReturn( "vendor name" );
            when( application.getVendorUrl() ).thenReturn( "https://vendor.url" );
            when( application.getUrl() ).thenReturn( "https://myapp.url" );
            when( application.getVersion() ).thenReturn( Version.parseVersion( "1.0.0" ) );
            when( application.getMinSystemVersion() ).thenReturn( "2.0.0" );
            when( application.getMaxSystemVersion() ).thenReturn( "3.0.0" );
            when( application.getModifiedTime() ).thenReturn( Instant.parse( "2020-09-25T10:00:00.00Z" ) );
            when( application.isStarted() ).thenReturn( true );

            when( applicationDescriptorService.get( applicationKey ) ).thenAnswer( descParams -> ApplicationDescriptor.create()
                .key( applicationKey )
                .description( "my app description" )
                .icon( Icon.from( new byte[]{0, 1}, "image/png", Instant.now() ) )
                .build() );

            return application;
        } );

        runScript( "/lib/xp/examples/app/create.js" );
    }
}
