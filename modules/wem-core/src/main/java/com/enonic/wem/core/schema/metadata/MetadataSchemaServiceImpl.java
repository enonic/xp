package com.enonic.wem.core.schema.metadata;

import javax.inject.Inject;

import com.enonic.wem.api.schema.metadata.GetMetadataSchemaParams;
import com.enonic.wem.api.schema.metadata.GetMetadataSchemasParams;
import com.enonic.wem.api.schema.metadata.MetadataSchema;
import com.enonic.wem.api.schema.metadata.MetadataSchemaService;
import com.enonic.wem.api.schema.metadata.MetadataSchemas;
import com.enonic.wem.core.schema.metadata.dao.MetadataSchemaDao;

public class MetadataSchemaServiceImpl
    implements MetadataSchemaService
{
    @Inject
    private MetadataSchemaDao metadataSchemaDao;

    @Override
    public MetadataSchema getByName( final GetMetadataSchemaParams params )
    {
        return new GetMetadataSchemaCommand().mixinDao( this.metadataSchemaDao ).params( params ).execute();
    }

    @Override
    public MetadataSchemas getByNames( final GetMetadataSchemasParams params )
    {
        return new GetMetadataSchemasCommand().metadataDao( this.metadataSchemaDao ).params( params ).execute();
    }

    @Override
    public MetadataSchemas getAll()
    {
        return metadataSchemaDao.getAllMetadataSchemas();
    }
}
