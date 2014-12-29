package com.enonic.wem.core.schema.mixin;

import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.MixinName;
import com.enonic.wem.api.schema.mixin.MixinService;
import com.enonic.wem.api.schema.mixin.Mixins;

@Component(immediate = true)
public final class MixinServiceImpl
    implements MixinService
{
    private MixinRegistry registry;

    @Override
    public Mixin getByName( final MixinName name )
    {
        return this.registry.get( name );
    }

    @Override
    public Mixins getAll()
    {
        return Mixins.from( this.registry.getAll() );
    }

    @Override
    public Mixins getByModule( final ModuleKey moduleKey )
    {
        final Stream<Mixin> stream = this.registry.getAll().stream().filter( new Predicate<Mixin>()
        {
            @Override
            public boolean test( final Mixin mixin )
            {
                return mixin.getName().getModuleKey().equals( moduleKey );
            }
        } );

        return Mixins.from( stream.collect( Collectors.toList() ) );
    }

    @Reference
    public void setRegistry( final MixinRegistry registry )
    {
        this.registry = registry;
    }
}
