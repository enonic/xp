package com.enonic.xp.core.impl.app;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;

import static org.mockito.Mockito.mock;

class AppFilterServiceImplTest
{
    @Test
    void default_allows_all()
    {
        final AppConfig config = mock( AppConfig.class, invocation -> invocation.getMethod().getDefaultValue() );
        new AppFilterServiceImpl( config ).accept( ApplicationKey.from( "some.app" ) );
    }
}
