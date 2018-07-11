package com.enonic.xp.attachment;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import com.enonic.xp.support.AbstractImmutableEntitySet;

@Beta
public final class CreateAttachments
    extends AbstractImmutableEntitySet<CreateAttachment>
{
    private final ImmutableMap<String, CreateAttachment> map;

    private CreateAttachments( final Set<CreateAttachment> set )
    {
        super( ImmutableSet.copyOf( set ) );
        this.map = Maps.uniqueIndex( set, new ToNameFunction() );
    }

    public ImmutableList<String> getNames()
    {
        final Collection<String> names = Collections2.transform( this.set, new ToNameFunction() );
        return ImmutableList.copyOf( names );
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

    private final static class ToNameFunction
        implements Function<CreateAttachment, String>
    {
        @Override
        public String apply( final CreateAttachment value )
        {
            return value.getName();
        }
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
        private Set<CreateAttachment> contents = Sets.newLinkedHashSet();

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
