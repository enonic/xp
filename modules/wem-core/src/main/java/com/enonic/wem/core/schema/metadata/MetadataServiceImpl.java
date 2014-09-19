package com.enonic.wem.core.schema.metadata;

import javax.inject.Inject;

import com.enonic.wem.api.schema.metadata.GetMetadataParams;
import com.enonic.wem.api.schema.metadata.GetMetadatasParams;
import com.enonic.wem.api.schema.metadata.Metadata;
import com.enonic.wem.api.schema.metadata.MetadataService;
import com.enonic.wem.api.schema.metadata.Metadatas;
import com.enonic.wem.core.schema.metadata.dao.MetadataDao;

public class MetadataServiceImpl
    implements MetadataService
{
    @Inject
    private MetadataDao metadataDao;

    @Override
    public Metadata getByName( final GetMetadataParams params )
    {
        return new GetMetadataCommand().mixinDao( this.metadataDao ).params( params ).execute();
    }

    @Override
    public Metadatas getByNames( final GetMetadatasParams params )
    {
        return new GetMetadatasCommand().metadataDao( this.metadataDao ).params( params ).execute();
    }

    @Override
    public Metadatas getAll()
    {
        return metadataDao.getAllMetadatas();
    }
}
