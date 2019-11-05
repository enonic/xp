package com.enonic.xp.web.session.impl.ignite;

import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.CacheWriteSynchronizationMode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.web.session.impl.WebSessionConfig;

public class IgniteCacheConfigFactory
{
    private final static Logger LOG = LoggerFactory.getLogger( IgniteCacheConfigFactory.class );

    public static <T> CacheConfiguration<String, T> create( final String cacheName, final WebSessionConfig config )
    {
        final CacheConfiguration<String, T> cacheConfig = new CacheConfiguration<>();

        cacheConfig.setAtomicityMode( CacheAtomicityMode.ATOMIC );
        cacheConfig.setWriteSynchronizationMode( getWriteSyncMode( config.write_sync_mode() ) );
        cacheConfig.setName( cacheName );
        setCacheMode( config, cacheConfig );
        cacheConfig.setStatisticsEnabled( config.cache_stats_enabled() );
        return cacheConfig;
    }

    private static CacheWriteSynchronizationMode getWriteSyncMode( final String writeSyncMode )
    {
        final String writeSyncModeLowercase = writeSyncMode.toLowerCase();
        switch ( writeSyncModeLowercase )
        {
            case "full":
                return CacheWriteSynchronizationMode.FULL_SYNC;
            case "primary":
                return CacheWriteSynchronizationMode.PRIMARY_SYNC;
            default:
                LOG.warn( "Unknown write sync mode: " + writeSyncModeLowercase + ", using default: " + "primary" );
                return CacheWriteSynchronizationMode.PRIMARY_SYNC;
        }
    }

    private static void setCacheMode( final WebSessionConfig config, final CacheConfiguration<String, ?> cacheConfig )
    {
        final String cacheMode = config.cache_mode().toLowerCase();
        switch ( cacheMode )
        {
            case "replicated":
                cacheConfig.setCacheMode( CacheMode.REPLICATED );
                break;
            case "partitioned":
                cacheConfig.setCacheMode( CacheMode.PARTITIONED );
                cacheConfig.setBackups( config.cache_replicas() );
                break;
            default:
                LOG.warn( "Unknown cache mode: " + cacheMode + ", using default: " + "partitioned" );
                cacheConfig.setCacheMode( CacheMode.PARTITIONED );
        }
    }
}
