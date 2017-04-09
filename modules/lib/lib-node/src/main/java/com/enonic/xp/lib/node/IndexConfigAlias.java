package com.enonic.xp.lib.node;

import com.enonic.xp.index.IndexConfig;

public enum IndexConfigAlias
{
    MINIMAL( IndexConfig.MINIMAL ),
    PATH( IndexConfig.PATH ),
    BYTYPE( IndexConfig.BY_TYPE ),
    FULLTEXT( IndexConfig.FULLTEXT ),
    NONE( IndexConfig.NONE );

    private final IndexConfig config;

    IndexConfigAlias( final IndexConfig config )
    {
        this.config = config;
    }

    public static IndexConfig from( final String value )
    {
        return IndexConfigAlias.valueOf( value.toUpperCase() ).config;
    }
}
