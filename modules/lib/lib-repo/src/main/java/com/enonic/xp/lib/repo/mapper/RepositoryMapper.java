package com.enonic.xp.lib.repo.mapper;

import com.enonic.xp.repository.Repository;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public class RepositoryMapper
    implements MapSerializable
{
    private Repository repository;

    public RepositoryMapper( final Repository repository )
    {
        this.repository = repository;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.value( "id", repository.getId() );
    }
}
