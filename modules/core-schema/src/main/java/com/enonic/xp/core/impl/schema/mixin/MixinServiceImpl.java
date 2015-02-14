package com.enonic.xp.core.impl.schema.mixin;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.google.common.collect.Maps;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.MixinName;
import com.enonic.wem.api.schema.mixin.MixinProvider;
import com.enonic.wem.api.schema.mixin.MixinService;
import com.enonic.wem.api.schema.mixin.Mixins;

@Component(immediate = true)
public final class MixinServiceImpl
    implements MixinService
{
    private final Map<MixinName, Mixin> map;

    public MixinServiceImpl()
    {
        this.map = Maps.newConcurrentMap();
    }

    @Override
    public Mixin getByName( final MixinName name )
    {
        return this.map.get( name );
    }

    @Override
    public Mixin getByLocalName( final String localName )
    {
        return this.map.values().stream().
            filter( ( mixin ) -> mixin.getName().getLocalName().equals( localName ) ).
            findFirst().orElse( null );
    }

    @Override
    public Mixins getAll()
    {
        return Mixins.from( this.map.values() );
    }

    @Override
    public Mixins getByModule( final ModuleKey moduleKey )
    {
        final Stream<Mixin> stream = this.map.values().stream().
            filter( ( mixin ) -> mixin.getName().getModuleKey().equals( moduleKey ) );

        return Mixins.from( stream.collect( Collectors.toList() ) );
    }

    @Override
    public Mixins getByContentType( final ContentType contentType )
    {
        final Stream<Mixin> stream = this.map.values().stream().
            filter( ( mixin ) -> contentType.getMetadata().contains( mixin.getName() ) );

        return Mixins.from( stream.collect( Collectors.toList() ) );
    }

    @Reference(policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.MULTIPLE)
    public void addProvider( final MixinProvider provider )
    {
        for ( final Mixin value : provider.get() )
        {
            this.map.put( value.getName(), value );
        }
    }

    public void removeProvider( final MixinProvider provider )
    {
        for ( final Mixin value : provider.get() )
        {
            this.map.remove( value.getName() );
        }
    }
}
