package com.enonic.wem.core.schema.metadata;

import java.util.ArrayList;
import java.util.List;

import com.enonic.wem.api.schema.metadata.GetMetadataSchemasParams;
import com.enonic.wem.api.schema.metadata.MetadataSchema;
import com.enonic.wem.api.schema.metadata.MetadataSchemaName;
import com.enonic.wem.api.schema.metadata.MetadataSchemaNames;
import com.enonic.wem.api.schema.metadata.MetadataSchemaNotFoundException;
import com.enonic.wem.api.schema.metadata.MetadataSchemas;
import com.enonic.wem.core.schema.metadata.dao.MetadataSchemaDao;

import static com.enonic.wem.api.schema.metadata.MetadataSchemas.newMetadatas;

final class GetMetadataSchemasCommand
{
    private MetadataSchemaDao dao;

    private GetMetadataSchemasParams params;

    MetadataSchemas execute()
    {
        this.params.validate();

        return doExecute();
    }

    private MetadataSchemas doExecute()
    {
        final List<MetadataSchemaName> missingMetadatas = new ArrayList<>();
        final MetadataSchemas metadataSchemas = getMetadataSchemas( params.getNames(), missingMetadatas );

        if ( !missingMetadatas.isEmpty() )
        {
            throw new MetadataSchemaNotFoundException( MetadataSchemaNames.from( missingMetadatas ) );
        }

        return metadataSchemas;
    }

    private MetadataSchemas getMetadataSchemas( final MetadataSchemaNames metadataSchemaNames, final List<MetadataSchemaName> missingMetadatas )
    {
        final MetadataSchemas.Builder metadatas = newMetadatas();

        for ( MetadataSchemaName metadataSchemaName : metadataSchemaNames )
        {
            final MetadataSchema metadataSchema = dao.getMetadataSchema( metadataSchemaName );
            if ( metadataSchema != null )
            {
                metadatas.add( metadataSchema );
            }
            else
            {
                missingMetadatas.add( metadataSchemaName );
            }
        }
        return metadatas.build();
    }

    GetMetadataSchemasCommand metadataSchemaDao( final MetadataSchemaDao metadataSchemaDao )
    {
        this.dao = metadataSchemaDao;
        return this;
    }

    GetMetadataSchemasCommand params( final GetMetadataSchemasParams params )
    {
        this.params = params;
        return this;
    }
}
