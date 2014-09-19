package com.enonic.wem.api.schema.metadata;

import java.text.MessageFormat;

import com.google.common.base.Joiner;

import com.enonic.wem.api.exception.NotFoundException;

public final class MetadataSchemaNotFoundException
    extends NotFoundException
{
    public MetadataSchemaNotFoundException( final MetadataSchemaName metadataSchemaName )
    {
        super( "MetadataSchema [{0}] was not found", metadataSchemaName );
    }

    public MetadataSchemaNotFoundException( final MetadataSchemaNames metadataSchemaNames )
    {
        super( MessageFormat.format( "MetadataSchemas with names [{0}] were not found", Joiner.on( ", " ).join( metadataSchemaNames ) ) );
    }
}
