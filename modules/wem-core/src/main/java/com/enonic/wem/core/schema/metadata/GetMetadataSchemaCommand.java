package com.enonic.wem.core.schema.metadata;

import com.enonic.wem.api.schema.metadata.GetMetadataSchemaParams;
import com.enonic.wem.api.schema.metadata.MetadataSchema;
import com.enonic.wem.api.schema.metadata.MetadataSchemaNotFoundException;
import com.enonic.wem.core.schema.metadata.dao.MetadataSchemaDao;

final class GetMetadataSchemaCommand
{
    private MetadataSchemaDao metadataSchemaDao;

    private GetMetadataSchemaParams params;

    public MetadataSchema execute()
    {
        this.params.validate();

        return doExecute();
    }

    private MetadataSchema doExecute()
    {
        final MetadataSchema metadataSchema = metadataSchemaDao.getMetadataSchema( params.getName() );
        if ( metadataSchema == null )
        {
            if ( params.isNotFoundAsException() )
            {
                throw new MetadataSchemaNotFoundException( params.getName() );
            }
            else
            {
                return null;
            }
        }
        else
        {
            return metadataSchema;
        }
    }

    GetMetadataSchemaCommand mixinDao( final MetadataSchemaDao metadataSchemaDao )
    {
        this.metadataSchemaDao = metadataSchemaDao;
        return this;
    }

    GetMetadataSchemaCommand params( final GetMetadataSchemaParams params )
    {
        this.params = params;
        return this;
    }
}
