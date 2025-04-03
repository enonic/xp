package com.enonic.xp.lib.repo.mapper;

import com.enonic.xp.branch.Branches;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.lib.common.PropertyTreeMapper;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public class RepositoryMapper
    implements MapSerializable
{
    private final Repository repository;

    public RepositoryMapper( final Repository repository )
    {
        this.repository = repository;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.value( "id", repository.getId() );
        gen.value( "transient", repository.isTransient() );
        serialize( gen, repository.getBranches() );
        serialize( "data", gen, repository.getData() );
    }

    private void serialize( final MapGenerator gen, final Branches branches )
    {
        gen.array( "branches" );
        branches.forEach( branch -> gen.value( branch.getValue() ) );
        gen.end();
    }


    private void serialize( String field, final MapGenerator gen, final PropertyTree repositoryData )
    {
        gen.map( field );
        new PropertyTreeMapper( repositoryData ).serialize( gen );
        gen.end();
    }
}
