package com.enonic.xp.audit;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

import com.enonic.xp.support.AbstractImmutableEntitySet;

@Beta
public final class AuditLogIds
    extends AbstractImmutableEntitySet<AuditLogId>
    implements Iterable<AuditLogId>
{
    private AuditLogIds( final ImmutableSet<AuditLogId> set )
    {
        super( set );
    }

    public static AuditLogIds empty()
    {
        final ImmutableSet<AuditLogId> set = ImmutableSet.of();
        return new AuditLogIds( set );
    }

    public static AuditLogIds from( final AuditLogId... ids )
    {
        return new AuditLogIds( ImmutableSet.copyOf( ids ) );
    }

    public static AuditLogIds from( final String... ids )
    {
        return new AuditLogIds( parseIds( ids ) );
    }

    public static AuditLogIds from( final Collection<String> ids )
    {
        return new AuditLogIds( doParseIds( ids ) );
    }

    public static AuditLogIds from( final Iterable<AuditLogId> ids )
    {
        return new AuditLogIds( ImmutableSet.copyOf( ids ) );
    }

    private static ImmutableSet<AuditLogId> parseIds( final String... ids )
    {
        final Collection<String> list = Lists.newArrayList( ids );
        return doParseIds( list );
    }

    private static ImmutableSet<AuditLogId> doParseIds( final Collection<String> list )
    {
        final Collection<AuditLogId> pathList = Collections2.transform( list, new ParseFunction() );
        return ImmutableSet.copyOf( pathList );
    }

    public Set<String> asStrings()
    {
        return this.set.stream().map( AuditLogId::toString ).collect( Collectors.toSet() );
    }

    public static Builder create()
    {
        return new Builder();
    }

    private final static class ParseFunction
        implements Function<String, AuditLogId>
    {
        @Override
        public AuditLogId apply( final String value )
        {
            return AuditLogId.from( value );
        }
    }

    public static class Builder
    {
        private List<AuditLogId> contents = Lists.newArrayList();

        public Builder add( final AuditLogId AuditLogId )
        {
            this.contents.add( AuditLogId );
            return this;
        }

        public Builder addAll( final AuditLogIds AuditLogIds )
        {
            this.contents.addAll( AuditLogIds.getSet() );
            return this;
        }


        public AuditLogIds build()
        {
            return AuditLogIds.from( contents );
        }
    }
}
