package com.enonic.xp.module.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;

import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleKeys;
import com.enonic.wem.api.module.ModuleNotFoundException;
import com.enonic.wem.api.module.ModuleVersion;
import com.enonic.wem.api.module.Modules;
import com.enonic.xp.module.impl.ModuleBuilder;
import com.enonic.xp.module.impl.ModuleRegistry;
import com.enonic.xp.module.impl.ModuleServiceImpl;

import static org.junit.Assert.*;

public class ModuleServiceImplTest
{
    private ModuleRegistry registry;

    private ModuleServiceImpl service;

    @Before
    public void setup()
    {
        this.registry = Mockito.mock( ModuleRegistry.class );
        this.service = new ModuleServiceImpl();
        this.service.setRegistry( this.registry );
    }

    private Module createModule( final String key )
    {
        return new ModuleBuilder().
            moduleKey( ModuleKey.from( key ) ).
            moduleVersion( ModuleVersion.from( "1.0.0" ) ).
            displayName( "module display name" ).
            url( "http://enonic.net" ).
            vendorName( "Enonic" ).
            vendorUrl( "https://www.enonic.com" ).
            build();
    }

    @Test
    public void testGetModule()
    {
        final Module module = createModule( "foomodule" );
        Mockito.when( this.registry.get( module.getKey() ) ).thenReturn( module );

        final Module result = this.service.getModule( ModuleKey.from( "foomodule" ) );
        assertSame( module, result );
    }

    @Test(expected = ModuleNotFoundException.class)
    public void testGetModule_notFound()
    {
        this.service.getModule( ModuleKey.from( "foomodule" ) );
    }

    @Test
    public void testGetAllModules()
    {
        final Module module = createModule( "foomodule" );
        Mockito.when( this.registry.getAll() ).thenReturn( Lists.newArrayList( module ) );

        final Modules result = this.service.getAllModules();
        assertNotNull( result );
        assertEquals( 1, result.getSize() );
        assertSame( module, result.get( 0 ) );
    }

    @Test
    public void testGetModules()
    {
        final Module module = createModule( "foomodule" );
        Mockito.when( this.registry.get( module.getKey() ) ).thenReturn( module );

        final Modules result = this.service.getModules( ModuleKeys.from( "foomodule", "othermodule" ) );
        assertNotNull( result );
        assertEquals( 1, result.getSize() );
        assertSame( module, result.get( 0 ) );
    }
}
