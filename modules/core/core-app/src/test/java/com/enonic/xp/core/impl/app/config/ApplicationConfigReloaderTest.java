package com.enonic.xp.core.impl.app.config;

import org.junit.jupiter.api.Test;
import org.osgi.framework.Bundle;

import com.enonic.xp.config.ConfigBuilder;
import com.enonic.xp.core.impl.app.ApplicationRegistry;
import com.enonic.xp.core.internal.Dictionaries;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ApplicationConfigReloaderTest
{
    @Test
    void updated_null_config_becomes_empty()
    {
        final Bundle bundle = mock( Bundle.class );
        final ApplicationRegistry serviceMock = mock( ApplicationRegistry.class );
        new ApplicationConfigReloader( bundle, serviceMock ).updated( null );
        verify( serviceMock ).configureApplication( same( bundle ), eq( ConfigBuilder.create().build() ) );
    }

    @Test
    void updated()
    {
        final Bundle bundle = mock( Bundle.class );
        final ApplicationRegistry serviceMock = mock( ApplicationRegistry.class );
        new ApplicationConfigReloader( bundle, serviceMock ).updated( Dictionaries.of( "a", "b" ) );
        verify( serviceMock ).configureApplication( same( bundle ), eq( ConfigBuilder.create().add( "a", "b" ).build() ) );
    }
}
