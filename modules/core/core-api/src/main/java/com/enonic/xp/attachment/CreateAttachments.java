package com.enonic.xp.attachment;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntitySet;

@PublicApi
public final class CreateAttachments
    extends AbstractImmutableEntitySet<CreateAttachment>
{
    private final ImmutableMap<String, CreateAttachment> map;

    private CreateAttachments( final Set<CreateAttachment> set )
    {
        super( ImmutableSet.copyOf( set ) );
        this.map = set.stream().collect( ImmutableMap.toImmutableMap( CreateAttachment::getName, Function.identity() ) );
    }

    public ImmutableList<String> getNames()
    {
        return map.keySet().asList();
    }

    public CreateAttachment getByName( final String name )
    {
        return this.map.get( name );
    }

    public static CreateAttachments empty()
    {
        final ImmutableSet<CreateAttachment> set = ImmutableSet.of();
        return new CreateAttachments( set );
    }

    public static CreateAttachments from( final CreateAttachment... contents )
    {
        return new CreateAttachments( ImmutableSet.copyOf( contents ) );
    }

    public static CreateAttachments from( final Iterable<? extends CreateAttachment> contents )
    {
        return new CreateAttachments( ImmutableSet.copyOf( contents ) );
    }

    public static CreateAttachments from( final Collection<? extends CreateAttachment> contents )
    {
        return new CreateAttachments( ImmutableSet.copyOf( contents ) );
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        if ( !super.equals( o ) )
        {
            return false;
        }
        final CreateAttachments that = (CreateAttachments) o;
        return Objects.equals( map, that.map );
    }

    @Override
    public int hashCode()
    {

        return Objects.hash( super.hashCode(), map );
    }

    public static class Builder
    {
        private Set<CreateAttachment> contents = new LinkedHashSet<>();

        public Builder add( CreateAttachment value )
        {
            contents.add( value );
            return this;
        }

        public Builder add( CreateAttachments value )
        {
            for ( final CreateAttachment ca : value )
            {
                contents.add( ca );
            }
            return this;
        }

        public CreateAttachments build()
        {
            return new CreateAttachments( contents );
        }
    }
}
