package com.enonic.wem.core.schema.metadata;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.schema.metadata.GetMetadataSchemaParams;
import com.enonic.wem.api.schema.metadata.GetMetadataSchemasParams;
import com.enonic.wem.api.schema.metadata.MetadataSchema;
import com.enonic.wem.api.schema.metadata.MetadataSchemaService;
import com.enonic.wem.api.schema.metadata.MetadataSchemas;
import com.enonic.wem.core.schema.metadata.dao.MetadataSchemaDao;

public class MetadataSchemaServiceImpl
    implements MetadataSchemaService
{
    private MetadataSchemaDao metadataSchemaDao;

    @Override
    public MetadataSchema getByName( final GetMetadataSchemaParams params )
    {
        return new GetMetadataSchemaCommand().metadataSchemaDao( this.metadataSchemaDao ).params( params ).execute();
    }

    @Override
    public MetadataSchemas getByNames( final GetMetadataSchemasParams params )
    {
        return new GetMetadataSchemasCommand().metadataSchemaDao( this.metadataSchemaDao ).params( params ).execute();
    }

    @Override
    public MetadataSchemas getByModule( final ModuleKey moduleKey )
    {
        return metadataSchemaDao.getByModule( moduleKey );
    }

    @Override
    public MetadataSchemas getAll()
    {
        return metadataSchemaDao.getAllMetadataSchemas();
    }

    public void setMetadataSchemaDao( final MetadataSchemaDao metadataSchemaDao )
    {
        this.metadataSchemaDao = metadataSchemaDao;
    }
}
