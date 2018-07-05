package com.enonic.xp.web.session.impl;

import java.lang.annotation.Annotation;
import java.util.Set;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.internal.util.future.IgniteFinishedFutureImpl;
import org.apache.ignite.lang.IgniteFuture;
import org.eclipse.jetty.server.session.SessionContext;
import org.eclipse.jetty.server.session.SessionData;
import org.eclipse.jetty.server.session.UnreadableSessionDataException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Sets;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class IgniteSessionDataStoreTest
{
    private final static String NODE = "mynode";

    private IgniteSessionDataStore store;

    private IgniteCache<String, SessionDataWrapper> cache;

    private Ignite ignite;

    @Before
    public void setup()
        throws Exception
    {
        store = new IgniteSessionDataStore();
        final SessionContext ctx = Mockito.mock( SessionContext.class );
        when( ctx.getCanonicalContextPath() ).thenReturn( "cpath" );
        when( ctx.getVhost() ).thenReturn( "vhost" );
        when( ctx.getWorkerName() ).thenReturn( NODE );
        mockContextRun( ctx );
        store.initialize( ctx );
        ignite = Mockito.mock( Ignite.class );
        cache = (IgniteCache<String, SessionDataWrapper>) Mockito.mock( IgniteCache.class );
        when( ignite.getOrCreateCache( any( CacheConfiguration.class ) ) ).thenReturn( cache );
        store.addIgnite( ignite );
        store.activate( getWebSessionConfig() );
    }

    @Test
    public void load()
        throws Exception
    {
        final SessionData sessionData = new SessionData( "123", null, null, 0, 0, 0, 0 );
        when( cache.get( anyString() ) ).thenReturn( new SessionDataWrapper( sessionData ) );

        Assert.assertEquals( sessionData, store.load( "123" ) );
    }

    @Test
    public void loadWithException()
        throws Exception
    {
        when( cache.get( anyString() ) ).thenThrow( new RuntimeException( "Something happened" ) );
        store.activate( getWebSessionConfig() );

        try
        {
            store.load( "123" );
            Assert.fail( "Expected exception" );
        }
        catch ( UnreadableSessionDataException e )
        {
            Assert.assertEquals( "123", e.getId() );
            // Expected exception here
        }
    }

    @Test
    public void delete()
        throws Exception
    {
        final IgniteFuture<Boolean> removeFuture = new IgniteFinishedFutureImpl<>( true );
        when( cache.removeAsync( anyString() ) ).thenReturn( removeFuture );
        store.activate( getWebSessionConfig() );

        final boolean deleted = store.delete( "123" );
        assertTrue( deleted );
    }

    @Test
    public void doStore()
        throws Exception
    {
        final SessionData sessionData = new SessionData( "123", null, null, 0, 0, 0, 0 );
        final IgniteFuture<Void> putFuture = new IgniteFinishedFutureImpl<>();
        when( cache.putAsync( eq( "cpath_vhost_123" ), any( SessionDataWrapper.class ) ) ).thenReturn( putFuture );
        store.activate( getWebSessionConfig() );

        store.doStore( "123", sessionData, 0 );

        verify( cache, Mockito.times( 1 ) ).putAsync( eq( "cpath_vhost_123" ), any( SessionDataWrapper.class ) );
    }

    @Test
    public void exists()
        throws Exception
    {
        final SessionData sessionData = new SessionData( "123", null, null, 0, 0, 0, 0 );
        when( cache.get( anyString() ) ).thenReturn( new SessionDataWrapper( sessionData ) );

        final boolean exists = store.exists( "123" );
        assertTrue( exists );
    }

    @Test
    public void existsFalse()
        throws Exception
    {
        final boolean exists = store.exists( "123" );
        assertFalse( exists );
    }

    @Test
    public void existsWithExpiryTime()
        throws Exception
    {
        final SessionData sessionData = new SessionData( "123", null, null, 0, 0, 0, 0 );
        sessionData.setExpiry( System.currentTimeMillis() + 1000 );
        when( cache.get( anyString() ) ).thenReturn( new SessionDataWrapper( sessionData ) );

        final boolean exists = store.exists( "123" );
        assertTrue( exists );
    }

    @Test
    public void isPassivating()
        throws Exception
    {
        assertTrue( store.isPassivating() );
    }

    @Test
    public void doGetExpired()
        throws Exception
    {
        final SessionData sessionData = new SessionData( "123", "cpath", "vhost", 0, 0, 0, 0 );
        sessionData.setExpiry( System.currentTimeMillis() - 10000 );
        sessionData.setLastNode( NODE );
        when( cache.get( eq( getCacheKey( "123" ) ) ) ).thenReturn( new SessionDataWrapper( sessionData ) );

        final SessionData sessionData2 = new SessionData( "456", "cpath", "vhost", 0, 0, 0, 0 );
        sessionData2.setExpiry( System.currentTimeMillis() - 1000000000 );
        sessionData2.setLastNode( "OTHER" );
        when( cache.get( eq( getCacheKey( "456" ) ) ) ).thenReturn( new SessionDataWrapper( sessionData2 ) );

        final Set<String> expiredSessionIds = store.doGetExpired( Sets.newHashSet( "123", "456", "789" ) );
        assertEquals( Sets.newHashSet( "123", "456", "789" ), expiredSessionIds );

        store.getExpired( Sets.newHashSet() );
        final Set<String> expiredSessionIds2 = store.doGetExpired( Sets.newHashSet( "123", "456", "789" ) );
        assertEquals( Sets.newHashSet( "123", "456", "789" ), expiredSessionIds2 );
    }


    @Test
    public void doGetExpiredEmpty()
        throws Exception
    {
        final Set<String> expiredSessionIds = store.doGetExpired( Sets.newHashSet() );
        assertEquals( Sets.newHashSet(), expiredSessionIds );
    }

    @Test
    public void withoutIgnite()
        throws Exception
    {
        final SessionData sessionData = new SessionData( "123", null, null, 0, 0, 0, 0 );
        when( cache.get( anyString() ) ).thenReturn( new SessionDataWrapper( sessionData ) );
        final IgniteFuture<Boolean> removeFuture = new IgniteFinishedFutureImpl<>( true );
        when( cache.removeAsync( anyString() ) ).thenReturn( removeFuture );
        final IgniteFuture<Void> putFuture = new IgniteFinishedFutureImpl<>();
        when( cache.putAsync( anyString(), any( SessionDataWrapper.class ) ) ).thenReturn( putFuture );

        store.removeIgnite( ignite );
        Assert.assertNull( store.load( "123" ) );
        Assert.assertFalse( store.delete( "123" ) );
        store.doStore( "123", sessionData, 0 );
        Assert.assertEquals( sessionData, store.load( "123" ) );
        Assert.assertTrue( store.delete( "123" ) );

        store.addIgnite( ignite );
        Assert.assertEquals( sessionData, store.load( "123" ) );
        Assert.assertTrue( store.delete( "123" ) );
        store.store( "123", sessionData );
        verify( cache, Mockito.times( 1 ) ).putAsync( eq( "cpath_vhost_123" ), any( SessionDataWrapper.class ) );
    }

    private void mockContextRun( final SessionContext context )
    {
        doAnswer( invocation -> {
            Runnable runnable = (Runnable) invocation.getArguments()[0];
            runnable.run();
            return null;
        } ).when( context ).run( any( Runnable.class ) );
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