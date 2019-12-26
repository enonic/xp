package com.enonic.xp.content;

import java.util.UUID;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.name.Name;

@PublicApi
public final class ContentName
    extends Name
{
    private final static String UNNAMED_PREFIX = "__unnamed__";

    private ContentName( final String name )
    {
        super( name );
    }

    public boolean isUnnamed()
    {
        return this.value.startsWith( UNNAMED_PREFIX );
    }

    public boolean hasUniqueness()
    {
        return isUnnamed() && this.value.length() > UNNAMED_PREFIX.length();
    }

    public static ContentName unnamed()
    {
        return from( UNNAMED_PREFIX );
    }

    public static ContentName from( final String name )
    {
        return new ContentName( name );
    }

    public static ContentName uniqueUnnamed()
    {
        return from( UNNAMED_PREFIX + UUID.randomUUID().toString() );
    }
}
