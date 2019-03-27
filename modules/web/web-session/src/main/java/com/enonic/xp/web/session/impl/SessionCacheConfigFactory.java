package com.enonic.xp.web.session.impl;

import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.CacheWriteSynchronizationMode;
import org.apache.ignite.cache.eviction.lru.LruEvictionPolicy;
import org.apache.ignite.configuration.CacheConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class SessionCacheConfigFactory
{
    private final static Logger LOG = LoggerFactory.getLogger( SessionCacheConfigFactory.class );

    static CacheConfiguration<String, SessionDataWrapper> create( final String cacheName, final WebSessionConfig config )
    {
        final CacheConfiguration<String, SessionDataWrapper> cacheConfig = new CacheConfiguration<>();

        cacheConfig.setAtomicityMode( CacheAtomicityMode.ATOMIC );
        cacheConfig.setWriteSynchronizationMode( getWriteSyncMode( config.write_sync_mode() ) );
        cacheConfig.setOnheapCacheEnabled( true );
        cacheConfig.setName( cacheName );
        setCacheMode( config, cacheConfig );
        cacheConfig.setStatisticsEnabled( config.cache_stats_enabled() );
        return cacheConfig;
    }

    private static CacheWriteSynchronizationMode getWriteSyncMode( final String writeSyncMode )
    {
        switch ( writeSyncMode.toLowerCase() )
        {
            case "full":
                return CacheWriteSynchronizationMode.FULL_SYNC;

            case "primary":
                return CacheWriteSynchronizationMode.PRIMARY_SYNC;
            case "async":
                return CacheWriteSynchronizationMode.FULL_ASYNC;
            default:
                LOG.warn( "Unknown write sync mode: " + writeSyncMode.toLowerCase() + ", using default: " + "FULL_SYNC" );
                return CacheWriteSynchronizationMode.FULL_SYNC;
        }
    }

    private static void setCacheMode( final WebSessionConfig config, final CacheConfiguration<String, SessionDataWrapper> cacheConfig )
    {
        switch ( config.cache_mode().toLowerCase() )
        {
            case "replicated":
                cacheConfig.setCacheMode( CacheMode.REPLICATED );
                break;
            case "local":
                cacheConfig.setCacheMode( CacheMode.LOCAL );
                break;
            case "partitioned":
                cacheConfig.setCacheMode( CacheMode.PARTITIONED );
                cacheConfig.setBackups( config.cache_replicas() );
                break;
            default:
                LOG.warn( "Unknown cache mode: " + config.cache_mode().toLowerCase() + ", using default: " + "REPLICATED" );
                cacheConfig.setCacheMode( CacheMode.REPLICATED );
        }
    }

    private static void setEvictionPolicy( final WebSessionConfig config, final CacheConfiguration<String, SessionDataWrapper> cacheConfig )
    {
        final LruEvictionPolicy evictPlc = new LruEvictionPolicy();
        evictPlc.setMaxSize( config.eviction_max_size() );

        cacheConfig.setEvictionPolicy( evictPlc );
    }

}
