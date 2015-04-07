package com.enonic.xp.core.impl.schema.content;

import java.time.Instant;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.collect.Maps;

import com.enonic.xp.schema.content.ContentTypeUpdatedEvent;
import com.enonic.xp.schema.content.ContentTypeDeletedEvent;
import com.enonic.xp.event.EventPublisher;
import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeProvider;
import com.enonic.xp.schema.content.ContentTypes;

final class ContentTypeRegistryImpl
    implements ContentTypeRegistry
{
    private final Map<ContentTypeName, ContentType> map;

    private EventPublisher eventPublisher;

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
            eventPublisher.publish( new ContentTypeUpdatedEvent ( value.getName(), Instant.now()) );
        }
    }

    @Override
    public void removeProvider( final ContentTypeProvider provider )
    {
        for ( final ContentType value : provider.get() )
        {
            this.map.remove( value.getName() );
            eventPublisher.publish( new ContentTypeDeletedEvent ( value.getName() ) );
        }
    }

    public void setEventPublisher( final EventPublisher eventPublisher )
    {
        this.eventPublisher = eventPublisher;
    }
}
