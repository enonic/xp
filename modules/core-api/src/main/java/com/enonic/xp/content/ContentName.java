package com.enonic.xp.content;


import java.util.UUID;

import com.google.common.base.Preconditions;

import com.enonic.xp.name.Name;

public class ContentName
    extends Name
{
    protected ContentName( final String name )
    {
        super( name );
    }

    public boolean isUnnamed()
    {
        return false;
    }

    private final static String UNNAMED_PREFIX = "__unnamed__";

    public static class Unnamed
        extends ContentName
    {
        public Unnamed( final String name )
        {
            super( name );
            Preconditions.checkArgument( name.startsWith( UNNAMED_PREFIX ),
                                         "An UnnamedContent must start with [" + UNNAMED_PREFIX + ": ]" + name );
        }

        @Override
        public boolean isUnnamed()
        {
            return true;
        }

        public static ContentName withUniqueness()
        {
            return new Unnamed( UNNAMED_PREFIX + UUID.randomUUID().toString() );
        }

        public boolean hasUniqueness()
        {
            return toString().length() > UNNAMED_PREFIX.length();
        }
    }

    public static ContentName from( final String name )
    {
        Preconditions.checkNotNull( name, "Cannot resolve ContentName from null" );
        if ( name.startsWith( UNNAMED_PREFIX ) )
        {
            return new Unnamed( name );
        }
        else
        {
            return new ContentName( name );
        }
    }
}
