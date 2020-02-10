package com.enonic.xp.core.impl.app.config;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.config.ConfigBuilder;
import com.enonic.xp.core.impl.app.ApplicationConfigService;
import com.enonic.xp.core.internal.Dictionaries;

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
        new ApplicationConfigReloader( appKey, serviceMock ).updated( Dictionaries.of( "a", "b" ) );
        verify( serviceMock ).setConfiguration( eq( appKey ), eq( ConfigBuilder.create().add( "a", "b" ).build() ) );
    }
}
