package com.enonic.xp.lib.app;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationDescriptor;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.Applications;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.util.Version;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ListVirtualApplicationHandlerTest
    extends BaseAppHandlerTest
{
    @Test
    void testExample()
    {

        when( applicationService.list() ).thenAnswer( params -> {
            final Application application1 = mock( Application.class );

            when( application1.getKey() ).thenReturn( ApplicationKey.from( "app1" ) );
            when( application1.getDisplayName() ).thenReturn( "app display name" );
            when( application1.getVendorName() ).thenReturn( "vendor name" );
            when( application1.getVendorUrl() ).thenReturn( "https://vendor.url" );
            when( application1.getUrl() ).thenReturn( "https://myapp.url" );
            when( application1.getVersion() ).thenReturn( Version.parseVersion( "1.0.0" ) );
            when( application1.isStarted() ).thenReturn( true );
            when( application1.getMinSystemVersion() ).thenReturn( "2.0.0" );
            when( application1.getMaxSystemVersion() ).thenReturn( "3.0.0" );
            when( application1.getSystemVersion() ).thenReturn( "1.21.3" );
            when( application1.getModifiedTime() ).thenReturn( Instant.parse( "2020-09-25T10:00:00.00Z" ) );

            when( applicationDescriptorService.get( application1.getKey() ) ).thenAnswer( descParams -> ApplicationDescriptor.create()
                .key( application1.getKey() )
                .description( "my app description" )
                .icon( Icon.from( new byte[]{0, 1}, "image/png", Instant.now() ) )
                .build() );

            final Application application2 = mock( Application.class );

            when( application2.getKey() ).thenReturn( ApplicationKey.from( "app2" ) );
            when( application2.getDisplayName() ).thenReturn( "app display name 2" );
            when( application2.getVendorName() ).thenReturn( "vendor name 2" );
            when( application2.getVendorUrl() ).thenReturn( "https://vendor2.url" );
            when( application2.getUrl() ).thenReturn( "https://myapp2.url" );
            when( application2.getVersion() ).thenReturn( Version.parseVersion( "4.1.2" ) );
            when( application2.getSystemVersion() ).thenReturn( "1.2.33-SNAPSHOT" );
            when( application2.getMinSystemVersion() ).thenReturn( "5.3.11" );
            when( application2.getMaxSystemVersion() ).thenReturn( "3.0.6" );
            when( application2.getModifiedTime() ).thenReturn( Instant.parse( "2021-09-25T10:00:00.00Z" ) );
            when( application2.isSystem() ).thenReturn( true );

            when( applicationDescriptorService.get( application2.getKey() ) ).thenAnswer( descParams -> ApplicationDescriptor.create()
                .key( application2.getKey() )
                .description( "my app description 2" )
                .icon( null )
                .build() );

            return Applications.from( application1, application2 );
        } );

        runScript( "/lib/xp/examples/app/list.js" );
    }
}
