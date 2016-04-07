package com.enonic.xp.blobstore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.blob.BlobStoreProvider;
import com.enonic.xp.blob.BlobStoreProviders;
import com.enonic.xp.blob.ProviderConfig;
import com.enonic.xp.blobstore.cache.CachedBlobStore;
import com.enonic.xp.blobstore.config.BlobStoreConfig;
import com.enonic.xp.blobstore.readthrough.ReadThroughBlobStore;

public class BlobStoreFactory
{
    private final BlobStoreConfig config;

    private final BlobStoreProvider provider;

    private final BlobStoreProviders providers;

    private final static Logger LOG = LoggerFactory.getLogger( BlobStoreFactory.class );

    private BlobStoreFactory( final Builder builder )
    {
        config = builder.config;
        provider = builder.provider;
        providers = builder.providers;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public BlobStore execute()
    {
        final BlobStore providerStore = provider.get();

        BlobStore builtStore = providerStore;

        final ProviderConfig config = provider.config();

        builtStore = populateWithReadThroughIfApplicable( config, providerStore );

        if ( this.config.cache() )
        {
            builtStore = CachedBlobStore.create().
                memoryCapacity( this.config.memoryCapacity() ).
                sizeTreshold( this.config.cacheSizeThreshold() ).
                blobStore( builtStore ).
                build();
        }

        return builtStore;
    }

    private BlobStore populateWithReadThroughIfApplicable( final ProviderConfig config, final BlobStore providerStore )
    {
        if ( config.readThroughEnabled() )
        {
            LOG.info( "Setting up readthrough provider" );

            final String readThroughProviderName = config.readThroughProvider();

            final BlobStoreProvider readThroughProvider = this.providers.get( readThroughProviderName );

            if ( readThroughProvider == null )
            {
                LOG.warn( "Readthrough provider [" + readThroughProviderName + "] not found, skipping" );
            }
            else
            {
                LOG.info( "Readthrough provider [" + readThroughProviderName + "] registered successfully" );

                return ReadThroughBlobStore.create().
                    store( providerStore ).
                    readThroughStore( readThroughProvider.get() ).
                    sizeThreshold( config.readThroughSizeThreshold() ).
                    build();
            }
        }

        return providerStore;
    }

    public static final class Builder
    {
        private BlobStoreConfig config;

        private BlobStoreProvider provider;

        private BlobStoreProviders providers;

        private Builder()
        {
        }

        public Builder config( final BlobStoreConfig val )
        {
            config = val;
            return this;
        }

        public Builder provider( final BlobStoreProvider val )
        {
            provider = val;
            return this;
        }

        public Builder providers( final BlobStoreProviders val )
        {
            providers = val;
            return this;
        }

        public BlobStoreFactory build()
        {
            return new BlobStoreFactory( this );
        }
    }
}
