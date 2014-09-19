package com.enonic.wem.core.schema.metadata;

import com.enonic.wem.api.schema.metadata.GetMetadataParams;
import com.enonic.wem.api.schema.metadata.Metadata;
import com.enonic.wem.api.schema.metadata.MetadataNotFoundException;
import com.enonic.wem.core.schema.metadata.dao.MetadataDao;

final class GetMetadataCommand
{
    private MetadataDao metadataDao;

    private GetMetadataParams params;

    public Metadata execute()
    {
        this.params.validate();

        return doExecute();
    }

    private Metadata doExecute()
    {
        final Metadata metadata = metadataDao.getMetadata( params.getName() );
        if ( metadata == null )
        {
            if ( params.isNotFoundAsException() )
            {
                throw new MetadataNotFoundException( params.getName() );
            }
            else
            {
                return null;
            }
        }
        else
        {
            return metadata;
        }
    }

    GetMetadataCommand mixinDao( final MetadataDao metadataDao )
    {
        this.metadataDao = metadataDao;
        return this;
    }

    GetMetadataCommand params( final GetMetadataParams params )
    {
        this.params = params;
        return this;
    }
}
