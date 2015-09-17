package com.enonic.xp.app;

import java.util.Collection;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import com.enonic.xp.support.AbstractImmutableEntityList;

@Beta
public final class ApplicationKeys
    extends AbstractImmutableEntityList<ApplicationKey>
{
    private ApplicationKeys( final ImmutableList<ApplicationKey> list )
    {
        super( list );
    }

    public static ApplicationKeys from( final ApplicationKey... applicationKeys )
    {
        return new ApplicationKeys( ImmutableList.copyOf( applicationKeys ) );
    }

    public static ApplicationKeys from( final Iterable<? extends ApplicationKey> applicationKeys )
    {
        return new ApplicationKeys( ImmutableList.copyOf( applicationKeys ) );
    }

    public static ApplicationKeys from( final Collection<? extends ApplicationKey> applicationKeys )
    {
        return new ApplicationKeys( ImmutableList.copyOf( applicationKeys ) );
    }

    public static ApplicationKeys from( final String... applicationKeys )
    {
        return new ApplicationKeys( parseApplicationKeys( applicationKeys ) );
    }

    public static ApplicationKeys empty()
    {
        return new ApplicationKeys( ImmutableList.<ApplicationKey>of() );
    }

    private static ImmutableList<ApplicationKey> parseApplicationKeys( final String... applicationKeys )
    {
        final Collection<String> list = Lists.newArrayList( applicationKeys );
        final Collection<ApplicationKey> applicationKeyList = Collections2.transform( list, new ParseFunction() );
        return ImmutableList.copyOf( applicationKeyList );
    }

    private final static class ParseFunction
        implements Function<String, ApplicationKey>
    {
        @Override
        public ApplicationKey apply( final String value )
        {
            return ApplicationKey.from( value );
        }
    }

}
