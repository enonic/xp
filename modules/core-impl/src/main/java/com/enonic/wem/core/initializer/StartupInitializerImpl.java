package com.enonic.wem.core.initializer;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.google.common.collect.Lists;

import com.enonic.wem.api.initializer.DataInitializer;

@Component(immediate = true)
public final class StartupInitializerImpl
    implements StartupInitializer
{

    private final List<DataInitializer> initializers;

    public StartupInitializerImpl()
    {
        this.initializers = Lists.newArrayList();
    }

    public void cleanData()
        throws Exception
    {
    }

    public void initializeData()
        throws Exception
    {
        for ( final DataInitializer initializer : this.initializers )
        {
            initializer.initialize();
        }
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addInitializer( final DataInitializer initializer )
    {
        this.initializers.add( initializer );
    }

    public void removeInitializer( final DataInitializer initializer )
    {
        this.initializers.remove( initializer );
    }
}
