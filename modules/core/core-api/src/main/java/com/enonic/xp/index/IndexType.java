package com.enonic.xp.index;

import java.util.Locale;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public enum IndexType
{
    SEARCH( true ), VERSION( false ), BRANCH( false ), COMMIT( false );

    private final boolean dynamicTypes;

    private final String name;

    IndexType( final boolean dynamicTypes )
    {
        this.dynamicTypes = dynamicTypes;
        this.name = this.name().toLowerCase( Locale.ROOT );
    }

    public boolean isDynamicTypes()
    {
        return dynamicTypes;
    }

    public String getName()
    {
        return this.name;
    }
}
