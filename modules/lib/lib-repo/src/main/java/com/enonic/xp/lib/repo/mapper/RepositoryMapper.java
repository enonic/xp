package com.enonic.xp.lib.repo.mapper;

import com.enonic.xp.repository.RepositorySettings;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public class RepositoryMapper
    implements MapSerializable
{
    private RepositorySettings repositorySettings;

    public RepositoryMapper( final RepositorySettings repositorySettings )
    {
        this.repositorySettings = repositorySettings;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.value( "id", repositorySettings.getRepositoryId() );
    }
}
