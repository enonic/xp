package com.enonic.xp.audit;

import java.util.ArrayList;
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
public final class AuditLogUris
    extends AbstractImmutableEntitySet<AuditLogUri>
    implements Iterable<AuditLogUri>
{
    private AuditLogUris( final ImmutableSet<AuditLogUri> set )
    {
        super( set );
    }

    public static AuditLogUris empty()
    {
        final ImmutableSet<AuditLogUri> set = ImmutableSet.of();
        return new AuditLogUris( set );
    }

    public static AuditLogUris from( final AuditLogUri... uris )
    {
        return new AuditLogUris( ImmutableSet.copyOf( uris ) );
    }

    public static AuditLogUris from( final String... uris )
    {
        return new AuditLogUris( parseIds( uris ) );
    }

    public static AuditLogUris from( final Collection<String> uris )
    {
        return new AuditLogUris( doParseIds( uris ) );
    }

    public static AuditLogUris from( final Iterable<AuditLogUri> uris )
    {
        return new AuditLogUris( ImmutableSet.copyOf( uris ) );
    }

    private static ImmutableSet<AuditLogUri> parseIds( final String... uris )
    {
        final Collection<String> list = Lists.newArrayList( uris );
        return doParseIds( list );
    }

    private static ImmutableSet<AuditLogUri> doParseIds( final Collection<String> list )
    {
        final Collection<AuditLogUri> pathList = Collections2.transform( list, new ParseFunction() );
        return ImmutableSet.copyOf( pathList );
    }

    public Set<String> asStrings()
    {
        return this.set.stream().map( AuditLogUri::toString ).collect( Collectors.toSet() );
    }

    public static Builder create()
    {
        return new Builder();
    }

    private final static class ParseFunction
        implements Function<String, AuditLogUri>
    {
        @Override
        public AuditLogUri apply( final String value )
        {
            return AuditLogUri.from( value );
        }
    }

    public static class Builder
    {
        private List<AuditLogUri> contents = new ArrayList<>();

        public Builder add( final AuditLogUri AuditLogUri )
        {
            this.contents.add( AuditLogUri );
            return this;
        }

        public Builder addAll( final AuditLogUris AuditLogUris )
        {
            this.contents.addAll( AuditLogUris.getSet() );
            return this;
        }


        public AuditLogUris build()
        {
            return AuditLogUris.from( contents );
        }
    }
}
