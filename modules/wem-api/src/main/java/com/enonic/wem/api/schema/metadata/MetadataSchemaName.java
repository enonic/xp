package com.enonic.wem.api.schema.metadata;

import org.apache.commons.lang.StringUtils;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleBasedName;

public final class MetadataSchemaName
    extends ModuleBasedName
{
    public static MetadataSchemaName MENU = new MetadataSchemaName( ModuleKey.SYSTEM, "menu" );

    private MetadataSchemaName( final ModuleKey moduleKey, final String localName )
    {
        super( moduleKey, localName );
    }

    public static MetadataSchemaName from( final ModuleKey moduleKey, final String localName )
    {
        return new MetadataSchemaName( moduleKey, localName );
    }

    public static MetadataSchemaName from( final String metadataName )
    {
        final String moduleKey = StringUtils.substringBefore( metadataName, SEPARATOR );
        final String localName = StringUtils.substringAfter( metadataName, SEPARATOR );
        return new MetadataSchemaName( ModuleKey.from( moduleKey ), localName );
    }
}
