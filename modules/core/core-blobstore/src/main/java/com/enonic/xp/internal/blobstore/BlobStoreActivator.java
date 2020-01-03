package com.enonic.xp.internal.blobstore;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.blob.BlobStoreProvider;
import com.enonic.xp.internal.blobstore.config.BlobStoreConfig;

@Component(immediate = true)
public class BlobStoreActivator
{
    private final BlobStoreProviders providers = new BlobStoreProviders();

    ;

    private ServiceRegistration<BlobStore> blobStoreReg;

    private BlobStoreConfig config;

    private BundleContext context;

    private final static Logger LOG = LoggerFactory.getLogger( BlobStoreActivator.class );

    @Activate
    public void activate( final BundleContext context )
    {
        this.context = context;
        registerBlobStore( false );
    }

    private void registerBlobStore( final boolean force )
    {
        if ( !activatorHasBeenInitialized() )
        {
            return;
        }

        if ( this.blobStoreReg != null && !force )
        {
            return;
        }

        doRegister();
    }

    private void doRegister()
    {
        final String name = config.providerName();

        LOG.info( "Waiting for blobstore-provider [" + name + "]" );

        final BlobStoreProvider blobStoreProvider = this.providers.get( name );

        if ( blobStoreProvider == null )
        {
            return;
        }

        LOG.info( "Found blobstore-provider [" + name + "]" );

        if ( blobStoreProvider.config().readThroughEnabled() )
        {
            final String readThroughProviderName = blobStoreProvider.config().readThroughProvider();

            if ( this.providers.get( readThroughProviderName ) == null )
            {
                LOG.info( "Waiting for readThrough-provider [" + readThroughProviderName + "]" );
                return;
            }
        }

        if ( blobStoreProvider.get() != null )
        {
            final BlobStore blobStore = BlobStoreFactory.create().
                config( this.config ).
                provider( blobStoreProvider ).
                providers( this.providers ).
                build().
                execute();

            this.blobStoreReg = this.context.registerService( BlobStore.class, blobStore, null );

            LOG.info( "Registered blobstore [" + this.config.providerName() + "] successfully" );
        }
    }

    private boolean activatorHasBeenInitialized()
    {
        return this.context != null;
    }

    @Deactivate
    public void deactivate()
    {
        if ( this.blobStoreReg != null )
        {
            this.blobStoreReg.unregister();
        }
    }

    @Reference
    public void setConfig( final BlobStoreConfig config )
    {
        this.config = config;
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addProvider( final BlobStoreProvider provider )
    {
        this.providers.add( provider );
        this.registerBlobStore( false );
    }

    @SuppressWarnings("unused")
    public void removeProvider( final BlobStoreProvider provider )
    {
        this.providers.remove( provider );
        this.registerBlobStore( true );
    }
}
