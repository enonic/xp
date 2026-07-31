package com.enonic.xp.internal.blobstore.readthrough;

import java.time.Duration;
import java.util.concurrent.Executors;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.blob.EvictableBlobStore;
import com.enonic.xp.core.internal.concurrent.SimpleRecurringJobScheduler;
import com.enonic.xp.internal.blobstore.config.BlobStoreConfig;

/**
 * Periodically evicts the read-through blobstore down to its configured capacity.
 * Runs on every node, as the read-through store is node local.
 */
@Component(immediate = true)
public class ReadThroughEvictionScheduler
{
    private static final Logger LOG = LoggerFactory.getLogger( ReadThroughEvictionScheduler.class );

    private final SimpleRecurringJobScheduler scheduler;

    @Activate
    public ReadThroughEvictionScheduler( @Reference final BlobStore blobStore, @Reference final BlobStoreConfig config )
    {
        this.scheduler = new SimpleRecurringJobScheduler( Executors::newSingleThreadScheduledExecutor, "blobstore-evict-thread" );

        if ( blobStore instanceof EvictableBlobStore )
        {
            final Duration interval = Duration.parse( config.readThroughEvictInterval() );
            this.scheduler.scheduleWithFixedDelay( () -> evict( (EvictableBlobStore) blobStore ), interval, interval,
                                                   e -> LOG.warn( "Error while evicting read-through blobstore", e ), e -> LOG.error(
                    "Error while evicting read-through blobstore, no further attempts will be made", e ) );
        }
    }

    private static void evict( final EvictableBlobStore blobStore )
    {
        final long evicted = blobStore.evict();
        if ( evicted > 0 )
        {
            LOG.info( "Evicted [{}] blobs from read-through blobstore", evicted );
        }
    }

    @Deactivate
    void deactivate()
    {
        this.scheduler.shutdownNow();
    }
}
