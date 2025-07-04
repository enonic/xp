package com.enonic.xp.audit;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntitySet;

@PublicApi
public final class AuditLogIds
    extends AbstractImmutableEntitySet<AuditLogId>
    implements Iterable<AuditLogId>
{
    private static final AuditLogIds EMPTY = new AuditLogIds( ImmutableSet.of() );

    private AuditLogIds( final ImmutableSet<AuditLogId> set )
    {
        super( set );
    }

    public static AuditLogIds empty()
    {
        return EMPTY;
    }

    public static AuditLogIds from( final AuditLogId... ids )
    {
        return fromInternal( ImmutableSet.copyOf( ids ) );
    }

    public static AuditLogIds from( final String... ids )
    {
        return from( Arrays.asList( ids ) );
    }

    public static AuditLogIds from( final Collection<String> ids )
    {
        return ids.stream().map( AuditLogId::from ).collect( collector() );
    }

    public static AuditLogIds from( final Iterable<AuditLogId> ids )
    {
        return fromInternal( ImmutableSet.copyOf( ids ) );
    }

    public static Collector<AuditLogId, ?, AuditLogIds> collector()
    {
        return Collectors.collectingAndThen( ImmutableSet.toImmutableSet(), AuditLogIds::fromInternal );
    }

    private static AuditLogIds fromInternal( final ImmutableSet<AuditLogId> set )
    {
        return set.isEmpty() ? EMPTY : new AuditLogIds( set );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private final ImmutableSet.Builder<AuditLogId> contents = ImmutableSet.builder();

        public Builder add( final AuditLogId auditLogId )
        {
            this.contents.add( auditLogId );
            return this;
        }

        public Builder addAll( final Iterable<? extends AuditLogId> auditLogIds )
        {
            this.contents.addAll( auditLogIds );
            return this;
        }

        public AuditLogIds build()
        {
            return fromInternal( contents.build() );
        }
    }
}
