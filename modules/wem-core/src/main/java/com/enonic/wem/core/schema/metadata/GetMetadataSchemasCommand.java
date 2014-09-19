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
    private MetadataSchemaDao metadataSchemaDao;

    private GetMetadataSchemasParams params;

    MetadataSchemas execute()
    {
        this.params.validate();

        return doExecute();
    }

    private MetadataSchemas doExecute()
    {
        final List<MetadataSchemaName> missingMetadatas = new ArrayList<>();
        final MetadataSchemas metadataSchemas = getMetadatas( params.getNames(), missingMetadatas );

        if ( !missingMetadatas.isEmpty() )
        {
            throw new MetadataSchemaNotFoundException( MetadataSchemaNames.from( missingMetadatas ) );
        }

        return metadataSchemas;
    }

    private MetadataSchemas getMetadatas( final MetadataSchemaNames metadataSchemaNames, final List<MetadataSchemaName> missingMetadatas )
    {
        final MetadataSchemas.Builder metadatas = newMetadatas();

        for ( MetadataSchemaName metadataSchemaName : metadataSchemaNames )
        {
            final MetadataSchema metadataSchema = metadataSchemaDao.getMetadataSchema( metadataSchemaName );
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

    GetMetadataSchemasCommand metadataDao( final MetadataSchemaDao metadataSchemaDao )
    {
        this.metadataSchemaDao = metadataSchemaDao;
        return this;
    }

    GetMetadataSchemasCommand params( final GetMetadataSchemasParams params )
    {
        this.params = params;
        return this;
    }
}
