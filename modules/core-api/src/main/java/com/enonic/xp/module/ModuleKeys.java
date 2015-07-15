package com.enonic.xp.module;

import java.util.Collection;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.support.AbstractImmutableEntityList;

@Beta
public final class ModuleKeys
    extends AbstractImmutableEntityList<ApplicationKey>
{
    private ModuleKeys( final ImmutableList<ApplicationKey> list )
    {
        super( list );
    }

    public static ModuleKeys from( final ApplicationKey... applicationKeys )
    {
        return new ModuleKeys( ImmutableList.copyOf( applicationKeys ) );
    }

    public static ModuleKeys from( final Iterable<? extends ApplicationKey> moduleKeys )
    {
        return new ModuleKeys( ImmutableList.copyOf( moduleKeys ) );
    }

    public static ModuleKeys from( final Collection<? extends ApplicationKey> moduleKeys )
    {
        return new ModuleKeys( ImmutableList.copyOf( moduleKeys ) );
    }

    public static ModuleKeys from( final String... moduleKeys )
    {
        return new ModuleKeys( parseModuleKeys( moduleKeys ) );
    }

    public static ModuleKeys empty()
    {
        return new ModuleKeys( ImmutableList.<ApplicationKey>of() );
    }

    private static ImmutableList<ApplicationKey> parseModuleKeys( final String... moduleKeys )
    {
        final Collection<String> list = Lists.newArrayList( moduleKeys );
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
