package com.enonic.xp.core.impl.app.config;

import java.util.Hashtable;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.config.ConfigBuilder;
import com.enonic.xp.core.impl.app.ApplicationConfigService;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ApplicationConfigReloaderTest
{
    @Test
    public void updated_null_config_becomes_empty()
    {
        final ApplicationKey appKey = ApplicationKey.from( "app1" );
        final ApplicationConfigService serviceMock = mock( ApplicationConfigService.class );
        new ApplicationConfigReloader( appKey, serviceMock ).updated( null );
        verify( serviceMock ).setConfiguration( eq( appKey ), eq( ConfigBuilder.create().build() ) );
    }

    @Test
    public void updated()
    {
        final ApplicationKey appKey = ApplicationKey.from( "app1" );
        final ApplicationConfigService serviceMock = mock( ApplicationConfigService.class );
        new ApplicationConfigReloader( appKey, serviceMock ).updated( new Hashtable<>( Map.of( "a", "b" ) ) );
        verify( serviceMock ).setConfiguration( eq( appKey ), eq( ConfigBuilder.create().add( "a", "b" ).build() ) );
    }
}
