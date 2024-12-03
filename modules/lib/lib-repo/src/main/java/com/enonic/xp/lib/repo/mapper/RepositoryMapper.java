package com.enonic.xp.lib.repo.mapper;

import com.fasterxml.jackson.databind.JsonNode;

import com.enonic.xp.branch.Branches;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.IndexType;
import com.enonic.xp.lib.common.JsonToPropertyTreeTranslator;
import com.enonic.xp.lib.common.PropertyTreeMapper;
import com.enonic.xp.repository.IndexDefinition;
import com.enonic.xp.repository.IndexDefinitions;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositorySettings;
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
        serialize( gen, repository.getSettings() );
        serialize( gen, repository.getData() );
    }

    private void serialize( final MapGenerator gen, final Branches branches )
    {
        gen.array( "branches" );
        branches.forEach( branch -> gen.value( branch.getValue() ) );
        gen.end();
    }

    private void serialize( final MapGenerator gen, final RepositorySettings settings )
    {
        gen.map( "settings" );
        serialize( gen, settings.getIndexDefinitions() );
        gen.end();
    }

    private void serialize( final MapGenerator gen, final PropertyTree repositoryData )
    {
        gen.map( "data" );
        new PropertyTreeMapper( repositoryData ).serialize( gen );
        gen.end();
    }

    private void serialize( final MapGenerator gen, final IndexDefinitions indexDefinitions )
    {
        if ( indexDefinitions != null )
        {
            gen.map( "definitions" );
            for ( IndexType indexType : IndexType.values() )
            {
                final IndexDefinition indexDefinition = indexDefinitions.get( indexType );
                if ( indexDefinition != null )
                {
                    gen.map( indexType.getName() );

                    if ( indexDefinition.getSettings() != null )
                    {
                        gen.map( "settings" );
                        serialize( gen, indexDefinition.getSettings().getNode() );
                        gen.end();
                    }

                    if ( indexDefinition.getMapping() != null )
                    {

                        gen.map( "mapping" );
                        serialize( gen, indexDefinition.getMapping().getNode() );
                        gen.end();
                    }

                    gen.end();
                }
            }
            gen.end();
        }
    }

    private void serialize( final MapGenerator gen, final JsonNode jsonNode )
    {
        final PropertyTree propertyTree = JsonToPropertyTreeTranslator.translate( jsonNode );
        new PropertyTreeMapper( propertyTree ).serialize( gen );
    }
}
