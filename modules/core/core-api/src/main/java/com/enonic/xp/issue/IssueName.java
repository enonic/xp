package com.enonic.xp.issue;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.name.Name;

@NullMarked
public final class IssueName
    extends Name
{
    private IssueName( final String name )
    {
        super( name );
    }

    public static IssueName from( final String name )
    {
        return new IssueName( name );
    }
}
