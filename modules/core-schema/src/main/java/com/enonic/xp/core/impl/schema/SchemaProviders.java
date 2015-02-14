package com.enonic.xp.core.impl.schema;

import java.util.Hashtable;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.google.common.collect.Lists;

import com.enonic.wem.api.schema.content.ContentTypeProvider;
import com.enonic.wem.api.schema.mixin.MixinProvider;
import com.enonic.wem.api.schema.relationship.RelationshipTypeProvider;

public final class SchemaProviders
{
    private final BundleContext context;

    private final List<ServiceRegistration> registrations;

    public SchemaProviders( final Bundle bundle )
    {
        this.context = bundle.getBundleContext();
        this.registrations = Lists.newArrayList();
    }

    public void register( final MixinProvider provider )
    {
        registerService( MixinProvider.class, provider );
    }

    public void register( final RelationshipTypeProvider provider )
    {
        registerService( RelationshipTypeProvider.class, provider );
    }

    public void register( final ContentTypeProvider provider )
    {
        registerService( ContentTypeProvider.class, provider );
    }

    private <T> void registerService( final Class<T> type, final T instance )

    {
        if ( instance == null )
        {
            return;
        }

        this.registrations.add( this.context.registerService( type, instance, new Hashtable<>() ) );
    }

    public void unregisterAll()
    {
        for ( final ServiceRegistration reg : this.registrations )
        {
            reg.unregister();
        }
    }
}
