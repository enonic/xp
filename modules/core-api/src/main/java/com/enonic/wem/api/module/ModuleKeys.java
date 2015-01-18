package com.enonic.wem.api.module;

import java.util.Collection;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import com.enonic.wem.api.support.AbstractImmutableEntityList;

public final class ModuleKeys
    extends AbstractImmutableEntityList<ModuleKey>
{
    private ModuleKeys( final ImmutableList<ModuleKey> list )
    {
        super( list );
    }

    public static ModuleKeys from( final ModuleKey... moduleKeys )
    {
        return new ModuleKeys( ImmutableList.copyOf( moduleKeys ) );
    }

    public static ModuleKeys from( final Iterable<? extends ModuleKey> moduleKeys )
    {
        return new ModuleKeys( ImmutableList.copyOf( moduleKeys ) );
    }

    public static ModuleKeys from( final Collection<? extends ModuleKey> moduleKeys )
    {
        return new ModuleKeys( ImmutableList.copyOf( moduleKeys ) );
    }

    public static ModuleKeys from( final String... moduleKeys )
    {
        return new ModuleKeys( parseModuleKeys( moduleKeys ) );
    }

    public static ModuleKeys empty()
    {
        return new ModuleKeys( ImmutableList.<ModuleKey>of() );
    }

    private static ImmutableList<ModuleKey> parseModuleKeys( final String... moduleKeys )
    {
        final Collection<String> list = Lists.newArrayList( moduleKeys );
        final Collection<ModuleKey> moduleKeyList = Collections2.transform( list, new ParseFunction() );
        return ImmutableList.copyOf( moduleKeyList );
    }

    private final static class ParseFunction
        implements Function<String, ModuleKey>
    {
        @Override
        public ModuleKey apply( final String value )
        {
            return ModuleKey.from( value );
        }
    }

}
