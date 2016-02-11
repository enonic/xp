package com.enonic.xp.blobstore;

import java.util.Hashtable;

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
import com.enonic.xp.blob.BlobStoreProviders;
import com.enonic.xp.blobstore.config.BlobStoreConfig;

@Component(immediate = true)
public class BlobStoreActivator
{
    private final BlobStoreProviders providers;

    private ServiceRegistration<BlobStore> blobStoreReg;

    private BlobStoreConfig config;

    private BundleContext context;

    private final static Logger LOG = LoggerFactory.getLogger( BlobStoreActivator.class );

    public BlobStoreActivator()
    {
        this.providers = new BlobStoreProviders();
    }

    @Activate
    public void activate( final BundleContext context )
    {
        this.context = context;
        registerBlobStore( false );
    }

    private void registerBlobStore( final boolean force )
    {
        if ( !isActivated() )
        {
            return;
        }

        if ( this.blobStoreReg != null && !force )
        {
            return;
        }

        final BlobStoreProvider blobStoreProvider = this.providers.get( config.providerName() );

        doRegister( blobStoreProvider );
    }

    private void doRegister( final BlobStoreProvider blobStoreProvider )
    {
        if ( blobStoreProvider != null )
        {
            final BlobStore blobStore = BlobStoreFactory.create().
                config( this.config ).
                provider( blobStoreProvider ).
                providers( this.providers ).
                build().
                build();

            this.blobStoreReg = this.context.registerService( BlobStore.class, blobStore, new Hashtable<>() );

            LOG.info( "Registered blobstore [" + this.config.providerName() + "] successfully" );
        }
    }


    private boolean isActivated()
    {
        if ( this.context == null )
        {
            return false;
        }
        return true;
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
