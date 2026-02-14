package com.enonic.xp.core.impl.content.schema;

import java.util.Objects;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.core.impl.schema.SchemaHelper;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.schema.mixin.MixinDescriptor;
import com.enonic.xp.schema.mixin.MixinDescriptors;
import com.enonic.xp.schema.mixin.MixinName;
import com.enonic.xp.schema.mixin.MixinNames;
import com.enonic.xp.schema.mixin.MixinService;

@Component(immediate = true)
public final class MixinServiceImpl
    implements MixinService
{
    private final BuiltinMixinTypes builtInTypes;

    private final ApplicationService applicationService;

    private final MixinDescriptorLoader mixinLoader;

    @Activate
    public MixinServiceImpl( @Reference final ApplicationService applicationService, @Reference final ResourceService resourceService )
    {
        this.builtInTypes = new BuiltinMixinTypes();
        this.applicationService = applicationService;
        this.mixinLoader = new MixinDescriptorLoader( resourceService );
    }

    @Override
    public MixinDescriptor getByName( final MixinName name )
    {
        if ( SchemaHelper.isSystem( name.getApplicationKey() ) )
        {
            return this.builtInTypes.getMixinDescriptor( name );
        }

        return mixinLoader.get( name );
    }

    @Override
    public MixinDescriptors getByNames( final MixinNames names )
    {
        return names.stream().map( this::getByName ).filter( Objects::nonNull ).collect( MixinDescriptors.collector() );
    }

    @Override
    public MixinDescriptors getAll()
    {
        final MixinDescriptors.Builder builder = MixinDescriptors.create();
        builder.addAll( this.builtInTypes.getAll() );

        for ( final Application application : this.applicationService.getInstalledApplications() )
        {
            builder.addAll( getByApplication( application.getKey() ) );
        }

        return builder.build();
    }

    @Override
    public MixinDescriptors getByApplication( final ApplicationKey key )
    {
        if ( SchemaHelper.isSystem( key ) )
        {
            return this.builtInTypes.getAll()
                .stream()
                .filter( type -> type.getName().getApplicationKey().equals( key ) )
                .collect( MixinDescriptors.collector() );
        }

        return mixinLoader.findNames( key ).stream().map( this::getByName ).filter( Objects::nonNull ).collect( MixinDescriptors.collector() );
    }
}
