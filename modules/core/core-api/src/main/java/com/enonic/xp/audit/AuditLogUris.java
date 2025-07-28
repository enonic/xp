package com.enonic.xp.audit;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntitySet;

@PublicApi
public final class AuditLogUris
    extends AbstractImmutableEntitySet<AuditLogUri>
{
    public static final AuditLogUris EMPTY = new AuditLogUris( ImmutableSet.of() );

    private AuditLogUris( final ImmutableSet<AuditLogUri> set )
    {
        super( set );
    }

    public static AuditLogUris empty()
    {
        return EMPTY;
    }

    public static AuditLogUris from( final AuditLogUri... uris )
    {
        return fromInternal( ImmutableSet.copyOf( uris ) );
    }

    public static AuditLogUris from( final String... uris )
    {
        return from( Arrays.asList( uris ) );
    }

    public static AuditLogUris from( final Collection<String> uris )
    {
        return uris.stream().map( AuditLogUri::from ).collect( collector() );
    }

    public static AuditLogUris from( final Iterable<AuditLogUri> uris )
    {
        return uris instanceof AuditLogUris u ? u : fromInternal( ImmutableSet.copyOf( uris ) );
    }

    public static Collector<AuditLogUri, ?, AuditLogUris> collector()
    {
        return Collectors.collectingAndThen( ImmutableSet.toImmutableSet(), AuditLogUris::fromInternal );
    }

    private static AuditLogUris fromInternal( final ImmutableSet<AuditLogUri> set )
    {
        return set.isEmpty() ? EMPTY : new AuditLogUris( set );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private final ImmutableSet.Builder<AuditLogUri> contents = ImmutableSet.builder();

        public Builder add( final AuditLogUri auditLogUri )
        {
            this.contents.add( auditLogUri );
            return this;
        }

        public Builder addAll( final Iterable<? extends AuditLogUri> auditLogUris )
        {
            this.contents.addAll( auditLogUris );
            return this;
        }

        public AuditLogUris build()
        {
            return fromInternal( contents.build() );
        }
    }
}
