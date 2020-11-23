package com.enonic.xp.core.impl.app.filter;

import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AppFilterTest
{
    @Test
    void disallowAll()
    {
        assertFalse( new AppFilter( "!*" ).accept( ApplicationKey.from( "com.enonic.app.features" ) ) );
    }

    @Test
    void allowAll()
    {
        assertTrue( new AppFilter( "*" ).accept( ApplicationKey.from( "com.enonic.app.features" ) ) );
    }

    @Test
    void noMatchDisallow()
    {
        assertFalse( new AppFilter( "some.app.name" ).accept( ApplicationKey.from( "another.app.name" ) ) );
    }

    @Test
    void firstRuleWins()
    {
        assertTrue( new AppFilter( "some.app.*,!some.app.name" ).accept( ApplicationKey.from( "some.app.name" ) ) );
    }

    @Test
    void firstRuleWins_2()
    {
        assertFalse( new AppFilter( "!some.app.name,some.app.*" ).accept( ApplicationKey.from( "some.app.name" ) ) );
    }

    @Test
    void trim()
    {
        final AppFilter appFilter = new AppFilter( " some.app.* , some.other.app.name " );
        assertAll( () -> assertTrue( appFilter.accept( ApplicationKey.from( "some.app.name" ) ) ),
                   () -> assertTrue( appFilter.accept( ApplicationKey.from( "some.other.app.name" ) ) ) );
    }
}
