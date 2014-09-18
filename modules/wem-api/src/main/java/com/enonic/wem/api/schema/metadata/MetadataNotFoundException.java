package com.enonic.wem.api.schema.metadata;

import java.text.MessageFormat;

import com.google.common.base.Joiner;

import com.enonic.wem.api.exception.NotFoundException;

public final class MetadataNotFoundException
    extends NotFoundException
{
    public MetadataNotFoundException( final MetadataName metadataName )
    {
        super( "Metadata [{0}] was not found", metadataName );
    }

    public MetadataNotFoundException( final MetadataNames metadataNames )
    {
        super( MessageFormat.format( "Metadatas with names [{0}] were not found", Joiner.on( ", " ).join( metadataNames ) ) );
    }
}
