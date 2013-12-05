package com.enonic.wem.portal.script;

import java.util.Set;

import org.mozilla.javascript.NativeObject;

import com.google.common.collect.ImmutableSet;

public final class ControllerScript
{
    private final NativeObject root;

    public ControllerScript( final NativeObject root )
    {
        this.root = root;
    }

    public Set<String> getMethods()
    {
        return getKeys( getHandlers() );
    }

    private NativeObject getConfig()
    {
        return getObject( this.root, "config" );
    }

    private NativeObject getHandlers()
    {
        return getObject( getConfig(), "handlers" );
    }

    private NativeObject getObject( final NativeObject parent, final String key )
    {
        if ( parent == null )
        {
            return null;
        }

        final Object o = parent.get( key );
        return ( o instanceof NativeObject ) ? (NativeObject) o : null;
    }

    private Set<String> getKeys( final NativeObject parent )
    {
        if ( parent == null )
        {
            return ImmutableSet.of();
        }

        final ImmutableSet.Builder<String> set = ImmutableSet.builder();
        for ( final Object id : parent.getIds() )
        {
            set.add( id.toString() );
        }

        return set.build();
    }
}
