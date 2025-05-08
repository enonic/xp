package com.enonic.xp.attachment;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntitySet;

@PublicApi
public final class CreateAttachments
    extends AbstractImmutableEntitySet<CreateAttachment>
{
    private CreateAttachments( final Set<CreateAttachment> set )
    {
        super( ImmutableSet.copyOf( set ) );
    }

    @Deprecated
    public List<String> getNames()
    {
        return this.set.stream().map( CreateAttachment::getName ).collect( ImmutableList.toImmutableList() );
    }

    @Deprecated
    public CreateAttachment getByName( final String name )
    {
        return this.set.stream().filter( ca -> ca.getName().equals( name ) ).findAny().orElse( null );
    }

    public static CreateAttachments empty()
    {
        return new CreateAttachments( ImmutableSet.of() );
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
        return super.equals( o );
    }

    @Override
    public int hashCode()
    {
        return super.hashCode();
    }

    public static class Builder
    {
        private final Set<CreateAttachment> contents = new LinkedHashSet<>();

        public Builder add( CreateAttachment value )
        {
            contents.add( value );
            return this;
        }

        public Builder add( CreateAttachments value )
        {
            contents.addAll( value.set );
            return this;
        }

        public CreateAttachments build()
        {
            return new CreateAttachments( contents );
        }
    }
}
