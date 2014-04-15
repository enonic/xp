package com.enonic.wem.api.module;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertSame;

public class ModuleKeyResolverTest
{
    @Test(expected = ModuleNotFoundException.class)
    public void testEmpty()
    {
        ModuleKeyResolver.empty().resolve( ModuleName.from( "module1" ) );
    }

    @Test
    public void testSystem()
    {
        final ModuleKeyResolver resolver = ModuleKeyResolver.from( ModuleKey.from( "foomodule-1.0.0" ) );
        assertSame( ModuleKey.SYSTEM, resolver.resolve( ModuleName.from( "system" ) ) );
    }

    @Test(expected = ModuleNotFoundException.class)
    public void testNotFound()
    {
        final ModuleKeyResolver resolver = ModuleKeyResolver.from( ModuleKey.from( "foomodule-1.0.0" ) );
        resolver.resolve( ModuleName.from( "barmodule" ) );
    }

    @Test
    public void testResolved()
    {
        final ModuleKeyResolver resolver = ModuleKeyResolver.from( ModuleKey.from( "foomodule-1.0.0" ) );
        assertEquals( "foomodule-1.0.0", resolver.resolve( ModuleName.from( "foomodule" ) ).toString() );
    }
}
