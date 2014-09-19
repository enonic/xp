package com.enonic.wem.core.schema.metadata;

import java.util.ArrayList;
import java.util.List;

import com.enonic.wem.api.schema.metadata.GetMetadatasParams;
import com.enonic.wem.api.schema.metadata.Metadata;
import com.enonic.wem.api.schema.metadata.MetadataName;
import com.enonic.wem.api.schema.metadata.MetadataNames;
import com.enonic.wem.api.schema.metadata.MetadataNotFoundException;
import com.enonic.wem.api.schema.metadata.Metadatas;
import com.enonic.wem.core.schema.metadata.dao.MetadataDao;

import static com.enonic.wem.api.schema.metadata.Metadatas.newMetadatas;

final class GetMetadatasCommand
{
    private MetadataDao metadataDao;

    private GetMetadatasParams params;

    Metadatas execute()
    {
        this.params.validate();

        return doExecute();
    }

    private Metadatas doExecute()
    {
        final List<MetadataName> missingMetadatas = new ArrayList<>();
        final Metadatas metadatas = getMetadatas( params.getNames(), missingMetadatas );

        if ( !missingMetadatas.isEmpty() )
        {
            throw new MetadataNotFoundException( MetadataNames.from( missingMetadatas ) );
        }

        return metadatas;
    }

    private Metadatas getMetadatas( final MetadataNames metadataNames, final List<MetadataName> missingMetadatas )
    {
        final Metadatas.Builder metadatas = newMetadatas();

        for ( MetadataName metadataName : metadataNames )
        {
            final Metadata metadata = metadataDao.getMetadata( metadataName );
            if ( metadata != null )
            {
                metadatas.add( metadata );
            }
            else
            {
                missingMetadatas.add( metadataName );
            }
        }
        return metadatas.build();
    }

    GetMetadatasCommand metadataDao( final MetadataDao metadataDao )
    {
        this.metadataDao = metadataDao;
        return this;
    }

    GetMetadatasCommand params( final GetMetadatasParams params )
    {
        this.params = params;
        return this;
    }
}
