package com.enonic.xp.issue;

import com.enonic.xp.name.Name;

public class IssueName
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
