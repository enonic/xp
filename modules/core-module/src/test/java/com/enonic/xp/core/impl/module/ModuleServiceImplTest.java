package com.enonic.xp.core.impl.module;

import java.time.Instant;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;

import com.google.common.collect.Lists;

import com.enonic.xp.module.Module;
import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.module.ModuleKeys;
import com.enonic.xp.module.ModuleNotFoundException;
import com.enonic.xp.module.ModuleVersion;
import com.enonic.xp.module.Modules;

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
        final ModuleImpl module = new ModuleImpl();
        module.moduleKey = ModuleKey.from( key );
        module.moduleVersion = ModuleVersion.from( "1.0.0" );
        module.displayName = "module display name";
        module.url = "http://enonic.net";
        module.vendorName = "Enonic";
        module.vendorUrl = "https://www.enonic.com";

        final Bundle bundle = Mockito.mock( Bundle.class );
        Mockito.when( bundle.getState() ).thenReturn( Bundle.ACTIVE );
        Mockito.when( bundle.getLastModified() ).thenReturn( Instant.parse( "2012-01-01T00:00:00.00Z" ).toEpochMilli() );
        module.bundle = bundle;

        return module;
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

    @Test
    public void testStartModule()
        throws Exception
    {
        final Module module = createModule( "foomodule" );
        Mockito.when( this.registry.get( module.getKey() ) ).thenReturn( module );

        this.service.startModule( module.getKey() );
        Mockito.verify( module.getBundle() ).start();
    }

    @Test
    public void testStopModule()
        throws Exception
    {
        final Module module = createModule( "foomodule" );
        Mockito.when( this.registry.get( module.getKey() ) ).thenReturn( module );

        this.service.stopModule( module.getKey() );
        Mockito.verify( module.getBundle() ).stop();
    }
}
