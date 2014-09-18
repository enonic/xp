package com.enonic.wem.api.schema.metadata;

import org.apache.commons.lang.StringUtils;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.schema.SchemaKey;
import com.enonic.wem.api.schema.SchemaName;

public final class MetadataName
    extends SchemaName
{
    private MetadataName( final ModuleKey moduleKey, final String localName )
    {
        super( moduleKey, localName );
    }

    @Override
    public SchemaKey toSchemaKey()
    {
        return SchemaKey.from( this );
    }

    public static MetadataName from( final ModuleKey moduleKey, final String localName )
    {
        return new MetadataName( moduleKey, localName );
    }

    public static MetadataName from( final String metadataName )
    {
        final String moduleKey = StringUtils.substringBefore( metadataName, SEPARATOR );
        final String localName = StringUtils.substringAfter( metadataName, SEPARATOR );
        return new MetadataName( ModuleKey.from( moduleKey ), localName );
    }
}
