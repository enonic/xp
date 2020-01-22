package com.enonic.xp.index;

public enum IndexType
{
    SEARCH( true ), STORAGE( false ), COMMIT( false );

    private final boolean dynamicTypes;

    IndexType( final boolean dynamicTypes )
    {
        this.dynamicTypes = dynamicTypes;
    }

    public boolean isDynamicTypes()
    {
        return dynamicTypes;
    }

    public String getName()
    {
        return this.name().toLowerCase();
    }
}
