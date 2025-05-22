package com.enonic.xp.core.impl.app.descriptor;

import java.io.IOException;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.descriptor.DescriptorKeyLocator;
import com.enonic.xp.descriptor.DescriptorKeys;
import com.enonic.xp.descriptor.DescriptorLoader;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;

public class MyDescriptorLoader
    implements DescriptorLoader<MyDescriptor>
{
    private static final String PATH = "/mytype";

    ResourceService resourceService;

    boolean loadException = false;

    @Override
    public Class<MyDescriptor> getType()
    {
        return MyDescriptor.class;
    }

    @Override
    public DescriptorKeys find( final ApplicationKey key )
    {
        return DescriptorKeys.from( new DescriptorKeyLocator( this.resourceService, PATH, true ).findKeys( key ) );
    }

    @Override
    public ResourceKey toResource( final DescriptorKey key )
    {
        return ResourceKey.from( key.getApplicationKey(), PATH + "/" + key.getName() + "/" + key.getName() + ".xml" );
    }

    @Override
    public MyDescriptor load( final DescriptorKey key, final Resource resource )
        throws Exception
    {
        if ( this.loadException )
        {
            throw new IOException( "Failed to do stuff" );
        }

        return new MyDescriptor( key.toString() );
    }

    @Override
    public MyDescriptor createDefault( final DescriptorKey key )
    {
        return new MyDescriptor( key.toString() );
    }

    @Override
    public MyDescriptor postProcess( final MyDescriptor descriptor )
    {
        return descriptor;
    }
}
