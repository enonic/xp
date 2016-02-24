package com.enonic.xp.blobstore.swift;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.blob.BlobStoreProvider;
import com.enonic.xp.blob.ProviderConfig;
import com.enonic.xp.blobstore.swift.config.SwiftConfig;

@Component
public class SwiftBlobStoreProvider
    implements BlobStoreProvider
{
    private SwiftBlobStore blobStore;

    private SwiftConfig config;

    @Activate
    public void activate()
    {
        if ( this.config.user() == null )
        {
            return;
        }

        this.blobStore = SwiftBlobStore.create().
            container( config.container() ).
            domain( config.domain() ).
            endpoint( config.endpoint() ).
            password( config.password() ).
            user( config.user() ).
            projectId( config.projectId() ).
            build();
    }

    @Override
    public BlobStore get()
    {
        return this.blobStore;
    }

    @Override
    public String name()
    {
        return "swift";
    }

    @Override
    public ProviderConfig config()
    {
        return this.config;
    }

    @Reference
    public void setConfig( final SwiftConfig config )
    {
        this.config = config;
    }
}
