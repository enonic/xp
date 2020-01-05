package com.enonic.xp.web.session.impl.ignite;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

import javax.cache.Cache;

import org.eclipse.jetty.server.session.SessionContext;
import org.eclipse.jetty.server.session.SessionData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.enonic.xp.web.session.impl.WebSessionConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IgniteSessionDataStoreTest
{
    private final static String NODE = "mynode";

    private IgniteSessionDataStore store;

    @Mock
    private Cache<String, IgniteSessionData> cache;


    @BeforeEach
    void setup()
        throws Exception
    {
        final SessionContext ctx = Mockito.mock( SessionContext.class );
        when( ctx.getCanonicalContextPath() ).thenReturn( "cpath" );
        when( ctx.getVhost() ).thenReturn( "vhost" );
        lenient().doCallRealMethod().when( ctx ).run( any() );
        lenient().when( ctx.getWorkerName() ).thenReturn( NODE );

        store = new IgniteSessionDataStore( cache );
        store.initialize( ctx );
        store.start();
    }

    @Test
    void load()
        throws Exception
    {
        final SessionData sessionData = new SessionData( "123", null, null, 0, 0, 0, 0 );
        when( cache.get( anyString() ) ).thenReturn( new IgniteSessionData( sessionData ) );

        assertEquals( sessionData.getId(), store.load( "123" ).getId() );
    }

    @Test
    void loadWithException()
    {
        when( cache.get( anyString() ) ).thenThrow( new RuntimeException( "Something happened" ) );

        final RuntimeException runtimeException = assertThrows( RuntimeException.class, () -> store.load( "123" ) );
        assertEquals( "Something happened", runtimeException.getMessage() );
    }

    @Test
    void delete()
    {
        when( cache.remove( anyString() ) ).thenReturn( true );

        final boolean deleted = store.delete( "123" );
        assertTrue( deleted );
    }

    @Test
    void doStore()
    {
        final SessionData sessionData = new SessionData( "123", null, null, 0, 0, 0, 0 );

        store.doStore( "123", sessionData, 0 );

        verify( cache, Mockito.times( 1 ) ).put( eq( "cpath_vhost_123" ), any( IgniteSessionData.class ) );
    }

    @Test
    void exists()
        throws Exception
    {
        final SessionData sessionData = new SessionData( "123", null, null, 0, 0, 0, 0 );
        when( cache.get( anyString() ) ).thenReturn( new IgniteSessionData( sessionData ) );

        final boolean exists = store.exists( "123" );
        assertTrue( exists );
    }

    @Test
    void existsFalse()
        throws Exception
    {
        final boolean exists = store.exists( "123" );
        assertFalse( exists );
    }

    @Test
    void existsWithExpiryTime()
        throws Exception
    {
        final SessionData sessionData = new SessionData( "123", null, null, 0, 0, 0, 0 );
        sessionData.setExpiry( System.currentTimeMillis() + 1000 );
        when( cache.get( anyString() ) ).thenReturn( new IgniteSessionData( sessionData ) );

        final boolean exists = store.exists( "123" );
        assertTrue( exists );
    }

    @Test
    void isPassivating()
    {
        assertTrue( store.isPassivating() );
    }

    @Test
    void doGetExpired()
    {
        final SessionData sessionData = new SessionData( "123", "cpath", "vhost", 0, 0, 0, 0 );
        sessionData.setExpiry( System.currentTimeMillis() - 10000 );
        sessionData.setLastNode( NODE );
        when( cache.get( eq( getCacheKey( "123" ) ) ) ).thenReturn( new IgniteSessionData( sessionData ) );

        final SessionData sessionData2 = new SessionData( "456", "cpath", "vhost", 0, 0, 0, 0 );
        sessionData2.setExpiry( System.currentTimeMillis() - 10000000000L );
        sessionData2.setLastNode( "OTHER" );
        when( cache.get( eq( getCacheKey( "456" ) ) ) ).thenReturn( new IgniteSessionData( sessionData2 ) );

        when( cache.get( eq( getCacheKey( "789" ) ) ) ).thenReturn( null );

        final Set<String> expiredSessionIds = store.doGetExpired( Set.of( "123", "456", "789" ) );
        assertEquals( Set.of( "123", "456", "789" ), expiredSessionIds );

        store.getExpired( new HashSet<>() );
        final Set<String> expiredSessionIds2 = store.doGetExpired( Set.of( "123", "456", "789" ) );
        assertEquals( Set.of( "123", "456", "789" ), expiredSessionIds2 );
    }


    @Test
    void doGetExpiredEmpty()
    {
        final Set<String> expiredSessionIds = store.doGetExpired( new HashSet<>() );
        assertEquals( new HashSet<>(), expiredSessionIds );
    }

    private String getCacheKey( String id )
    {
        return "cpath_vhost_" + id;
    }

    WebSessionConfig getWebSessionConfig()
    {
        WebSessionConfig annotation = new WebSessionConfig()
        {

            @Override
            public Class<? extends Annotation> annotationType()
            {
                return null;
            }

            @Override
            public int retries()
            {
                return 3;
            }

            @Override
            public int eviction_max_size()
            {
                return 1000;
            }

            @Override
            public String write_sync_mode()
            {
                return "full";
            }

            @Override
            public String cache_mode()
            {
                return "replicated";
            }

            @Override
            public int cache_replicas()
            {
                return 1;
            }

            @Override
            public boolean cache_stats_enabled()
            {
                return false;
            }

            @Override
            public int session_save_period()
            {
                return 10;
            }

            public int write_timeout()
            {
                return 1000;
            }
        };

        return annotation;
    }
}
