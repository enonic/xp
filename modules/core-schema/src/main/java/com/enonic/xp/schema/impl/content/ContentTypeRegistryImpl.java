package com.enonic.xp.schema.impl.content;

import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Maps;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeProvider;
import com.enonic.wem.api.schema.content.ContentTypes;

final class ContentTypeRegistryImpl
    implements ContentTypeRegistry
{
    private final Map<ContentTypeName, ContentType> map;

    public ContentTypeRegistryImpl()
    {
        this.map = Maps.newConcurrentMap();
    }

    @Override
    public ContentType get( final ContentTypeName name )
    {
        return this.map.get( name );
    }

    @Override
    public ContentTypes getByModule( final ModuleKey moduleKey )
    {
        final Stream<ContentType> stream = this.map.values().stream().filter( new Predicate<ContentType>()
        {
            @Override
            public boolean test( final ContentType value )
            {
                return value.getName().getModuleKey().equals( moduleKey );
            }
        } );

        return ContentTypes.from( stream.collect( Collectors.toList() ) );
    }

    @Override
    public ContentTypes getAll()
    {
        return ContentTypes.from( this.map.values() );
    }

    @Override
    public void addProvider( final ContentTypeProvider provider )
    {
        for ( final ContentType value : provider.get() )
        {
            this.map.put( value.getName(), value );
        }
    }

    @Override
    public void removeProvider( final ContentTypeProvider provider )
    {
        for ( final ContentType value : provider.get() )
        {
            this.map.remove( value.getName() );
        }
    }
}
