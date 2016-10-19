package com.enonic.xp.lib.repo;

import java.util.Map;
import java.util.function.Supplier;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.enonic.xp.index.IndexType;
import com.enonic.xp.lib.repo.mapper.RepositoryMapper;
import com.enonic.xp.repository.CreateRepositoryParams;
import com.enonic.xp.repository.IndexDefinition;
import com.enonic.xp.repository.IndexDefinitions;
import com.enonic.xp.repository.IndexMapping;
import com.enonic.xp.repository.IndexSettings;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.repository.RepositorySettings;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

@SuppressWarnings("unused")
public class CreateRepositoryHandler
    implements ScriptBean
{
    private RepositoryId repositoryId;

    private IndexDefinitions indexDefinitions;

    private Supplier<RepositoryService> repositoryServiceSupplier;

    public void setRepositoryId( final String repositoryId )
    {
        this.repositoryId = repositoryId == null ? null : RepositoryId.from( repositoryId );
    }

    public void setIndexDefinitions( final ScriptValue data )
    {
        final Map<String, Object> indexDefinitionsMap = data.getMap();
        final IndexDefinitions.Builder indexDefinitionsBuilder = IndexDefinitions.create();
        for ( IndexType indexType : IndexType.values() )
        {
            final Map indexDefinitionMap = (Map) indexDefinitionsMap.get( indexType.getName() );
            if ( indexDefinitionMap != null )
            {
                final Map indexDefinitionSettingsMap = (Map) indexDefinitionMap.get( "settings" );
                IndexSettings indexSettings =
                    indexDefinitionSettingsMap == null ? null : new IndexSettings( createJson( indexDefinitionSettingsMap ) );
                final Map indexDefinitionMappingMap = (Map) indexDefinitionMap.get( "mapping" );
                IndexMapping indexMapping =
                    indexDefinitionMappingMap == null ? null : new IndexMapping( createJson( indexDefinitionMappingMap ) );
                final IndexDefinition indexDefinition = IndexDefinition.create().settings( indexSettings ).mapping( indexMapping ).build();
                indexDefinitionsBuilder.add( indexType, indexDefinition );
            }
        }
        this.indexDefinitions = indexDefinitionsBuilder.build();
    }

    private JsonNode createJson( final Map<?, ?> value )
    {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.valueToTree( value );
    }

    public RepositoryMapper execute()
    {
        final RepositorySettings repositorySettings = RepositorySettings.create().
            indexDefinitions( indexDefinitions ).
            build();

        final CreateRepositoryParams createRepositoryParams = CreateRepositoryParams.create().
            repositoryId( repositoryId ).
            repositorySettings( repositorySettings ).
            build();

        final Repository repository = repositoryServiceSupplier.
            get().
            createRepository( createRepositoryParams );

        return repository == null ? null : new RepositoryMapper( repository );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        repositoryServiceSupplier = context.getService( RepositoryService.class );
    }
}
