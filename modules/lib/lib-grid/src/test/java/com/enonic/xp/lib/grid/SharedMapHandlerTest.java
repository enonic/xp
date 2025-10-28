package com.enonic.xp.lib.grid;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.shared.SharedMap;
import com.enonic.xp.shared.SharedMapService;
import com.enonic.xp.testing.ScriptTestSupport;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SharedMapHandlerTest
    extends ScriptTestSupport
{
    private SharedMapService sharedMapService;

    private SharedMap<String, String> sharedMap;

    @Override
    protected void initialize()
        throws Exception
    {
        super.initialize();

        this.sharedMapService = mock( SharedMapService.class );
        addService( SharedMapService.class, sharedMapService );
    }

    @BeforeEach
    void setUp()
    {
        this.sharedMap = mock( SharedMap.class );

        when( sharedMapService.<String, String>getSharedMap( "mapId" ) ).thenReturn( sharedMap );
    }

    @Test
    void testGetWithoutKey()
    {
        runFunction( "/test/grid-test.js", "testGetWithoutKey" );
        verify( sharedMapService, times( 1 ) ).getSharedMap( "mapId" );
    }

    @Test
    void testGet()
    {
        when( sharedMap.get( "key" ) ).thenReturn( "value" );

        runFunction( "/test/grid-test.js", "testGet" );

        verify( sharedMapService, times( 1 ) ).getSharedMap( "mapId" );
        verify( sharedMap, times( 1 ) ).get( "key" );
    }

    @Test
    void testDeleteWithoutKey()
    {
        runFunction( "/test/grid-test.js", "testDeleteWithoutKey" );
        verify( sharedMapService, times( 1 ) ).getSharedMap( "mapId" );
    }

    @Test
    void testDelete()
    {
        doNothing().when( sharedMap ).delete( "key" );

        runFunction( "/test/grid-test.js", "testDelete" );

        verify( sharedMapService, times( 1 ) ).getSharedMap( "mapId" );
        verify( sharedMap, times( 1 ) ).delete( "key" );
    }

    @Test
    void testSetWithoutKey()
    {
        runFunction( "/test/grid-test.js", "testSetWithoutKey" );

        verify( sharedMapService, times( 1 ) ).getSharedMap( "mapId" );
    }

    @Test
    void testSetWithoutTtlSeconds()
    {
        doNothing().when( sharedMap ).set( "key", "value" );

        runFunction( "/test/grid-test.js", "testSetWithoutTtlSeconds" );

        verify( sharedMapService, times( 1 ) ).getSharedMap( "mapId" );
        verify( sharedMap, times( 1 ) ).set( "key", "value" );
    }

    @Test
    void testSet()
    {
        doNothing().when( sharedMap ).set( "key", "value", 2 * 60 * 1000 );

        runFunction( "/test/grid-test.js", "testSet" );

        verify( sharedMapService, times( 1 ) ).getSharedMap( "mapId" );
        verify( sharedMap, times( 1 ) ).set( "key", "value", 2 * 60 * 1000 );
    }

    @Test
    void testSetWithNullValue()
    {
        doNothing().when( sharedMap ).set( "key", null, -1 );

        runFunction( "/test/grid-test.js", "testSetWithNullValue" );

        verify( sharedMapService, times( 1 ) ).getSharedMap( "mapId" );
        verify( sharedMap, times( 1 ) ).set( "key", null, -1 );
    }

    @Test
    void testModifyWithoutKey()
    {
        runFunction( "/test/grid-test.js", "testModifyWithoutKey" );

        verify( sharedMapService, times( 1 ) ).getSharedMap( "mapId" );
    }

    @Test
    void testModifyWithoutFunc()
    {
        runFunction( "/test/grid-test.js", "testModifyWithoutFunc" );

        verify( sharedMapService, times( 1 ) ).getSharedMap( "mapId" );
    }

    @Test
    void testModifyWithWrongArgumentFunc()
    {
        runFunction( "/test/grid-test.js", "testModifyWithWrongArgumentFunc" );

        verify( sharedMapService, times( 1 ) ).getSharedMap( "mapId" );
    }
}
