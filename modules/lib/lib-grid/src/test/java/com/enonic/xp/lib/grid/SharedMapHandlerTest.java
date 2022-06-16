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

public class SharedMapHandlerTest
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
    public void setUp()
    {
        this.sharedMap = mock( SharedMap.class );

        when( sharedMapService.<String, String>getSharedMap( "mapId" ) ).thenReturn( sharedMap );
    }

    @Test
    public void testGetWithoutKey()
    {
        runFunction( "/test/grid-test.js", "testGetWithoutKey" );
        verify( sharedMapService, times( 1 ) ).getSharedMap( "mapId" );
    }

    @Test
    public void testGet()
    {
        when( sharedMap.get( "key" ) ).thenReturn( "value" );

        runFunction( "/test/grid-test.js", "testGet" );

        verify( sharedMapService, times( 1 ) ).getSharedMap( "mapId" );
        verify( sharedMap, times( 1 ) ).get( "key" );
    }

    @Test
    public void testDeleteWithoutKey()
    {
        runFunction( "/test/grid-test.js", "testDeleteWithoutKey" );
        verify( sharedMapService, times( 1 ) ).getSharedMap( "mapId" );
    }

    @Test
    public void testDelete()
    {
        doNothing().when( sharedMap ).delete( "key" );

        runFunction( "/test/grid-test.js", "testDelete" );

        verify( sharedMapService, times( 1 ) ).getSharedMap( "mapId" );
        verify( sharedMap, times( 1 ) ).delete( "key" );
    }

    @Test
    public void testSetWithoutKey()
    {
        runFunction( "/test/grid-test.js", "testSetWithoutKey" );

        verify( sharedMapService, times( 1 ) ).getSharedMap( "mapId" );
    }

    @Test
    public void testSetWithoutTtlSeconds()
    {
        doNothing().when( sharedMap ).set( "key", "value" );

        runFunction( "/test/grid-test.js", "testSetWithoutTtlSeconds" );

        verify( sharedMapService, times( 1 ) ).getSharedMap( "mapId" );
        verify( sharedMap, times( 1 ) ).set( "key", "value" );
    }

    @Test
    public void testSet()
    {
        doNothing().when( sharedMap ).set( "key", "value", 2 * 60 * 1000 );

        runFunction( "/test/grid-test.js", "testSet" );

        verify( sharedMapService, times( 1 ) ).getSharedMap( "mapId" );
        verify( sharedMap, times( 1 ) ).set( "key", "value", 2 * 60 * 1000 );
    }

    @Test
    public void testSetWithNullValue()
    {
        doNothing().when( sharedMap ).set( "key", null, -1 );

        runFunction( "/test/grid-test.js", "testSetWithNullValue" );

        verify( sharedMapService, times( 1 ) ).getSharedMap( "mapId" );
        verify( sharedMap, times( 1 ) ).set( "key", null, -1 );
    }

    @Test
    public void testModifyWithoutKey()
    {
        runFunction( "/test/grid-test.js", "testModifyWithoutKey" );

        verify( sharedMapService, times( 1 ) ).getSharedMap( "mapId" );
    }

    @Test
    public void testModifyWithoutFunc()
    {
        runFunction( "/test/grid-test.js", "testModifyWithoutFunc" );

        verify( sharedMapService, times( 1 ) ).getSharedMap( "mapId" );
    }

    @Test
    public void testModifyWithWrongArgumentFunc()
    {
        runFunction( "/test/grid-test.js", "testModifyWithWrongArgumentFunc" );

        verify( sharedMapService, times( 1 ) ).getSharedMap( "mapId" );
    }
}
