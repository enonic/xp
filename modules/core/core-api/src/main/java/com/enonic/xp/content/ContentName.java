package com.enonic.xp.content;

import java.util.UUID;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.name.Name;

@PublicApi
public final class ContentName
    extends Name
{
    private static final String UNNAMED_PREFIX = "__unnamed__";

    private ContentName( final String name, boolean validate )
    {
        super( name, validate );
    }

    public boolean isUnnamed()
    {
        return this.value.startsWith( UNNAMED_PREFIX );
    }

    public boolean hasUniqueness()
    {
        return isUnnamed() && this.value.length() > UNNAMED_PREFIX.length();
    }

    public static ContentName from( final String name )
    {
        return new ContentName( name, true );
    }

    public static ContentName uniqueUnnamed()
    {
        return new ContentName( UNNAMED_PREFIX + UUID.randomUUID(), false );
    }
}
