package com.enonic.xp.lib.repo;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.enonic.xp.index.ChildOrder;
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
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;

@SuppressWarnings("unused")
public class CreateRepositoryHandler
    implements ScriptBean
{
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private RepositoryId repositoryId;

    private IndexDefinitions indexDefinitions;

    private AccessControlList rootPermissions;

    private Boolean inheritPermissions;

    private ChildOrder rootChildOrder;

    private Supplier<RepositoryService> repositoryServiceSupplier;

    public void setRepositoryId( final String repositoryId )
    {
        this.repositoryId = repositoryId == null ? null : RepositoryId.from( repositoryId );
    }

    public void setRootPermissions( final ScriptValue rootPermissions )
    {
        if ( rootPermissions != null )
        {
            final List<AccessControlEntry> accessControlEntries = rootPermissions.getArray().
                stream().
                map( this::convertToAccessControlEntry ).
                collect( Collectors.toList() );

            this.rootPermissions = AccessControlList.
                create().
                addAll( accessControlEntries ).
                build();
        }
    }

    public void setRootChildOrder( final String rootChildOrder )
    {
        if ( rootChildOrder != null )
        {
            this.rootChildOrder = ChildOrder.from( rootChildOrder );
        }
    }

    public void setIndexDefinitions( final ScriptValue data )
    {
        if ( data != null )
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
                    final IndexDefinition indexDefinition =
                        IndexDefinition.create().settings( indexSettings ).mapping( indexMapping ).build();
                    indexDefinitionsBuilder.add( indexType, indexDefinition );
                }
            }
            this.indexDefinitions = indexDefinitionsBuilder.build();
        }
    }

    public RepositoryMapper execute()
    {
        final RepositorySettings repositorySettings = RepositorySettings.create().
            indexDefinitions( indexDefinitions ).
            build();

        final CreateRepositoryParams createRepositoryParams = CreateRepositoryParams.create().
            repositoryId( repositoryId ).
            repositorySettings( repositorySettings ).
            rootPermissions( rootPermissions ).
            rootChildOrder( rootChildOrder ).
            build();

        final Repository repository = repositoryServiceSupplier.
            get().
            createRepository( createRepositoryParams );

        return repository == null ? null : new RepositoryMapper( repository );
    }

    private AccessControlEntry convertToAccessControlEntry( ScriptValue permission )
    {
        final String principal = permission.getMember( "principal" ).
            getValue( String.class );
        final List<Permission> allowedPermissions = permission.getMember( "allow" ).
            getArray( String.class ).
            stream().
            map( Permission::valueOf ).
            collect( Collectors.toList() );
        final List<Permission> deniedPermissions = permission.getMember( "deny" ).
            getArray( String.class ).
            stream().
            map( Permission::valueOf ).
            collect( Collectors.toList() );

        return AccessControlEntry.create().
            principal( PrincipalKey.from( principal ) ).
            allow( allowedPermissions ).
            deny( deniedPermissions ).
            build();
    }

    private JsonNode createJson( final Map<?, ?> value )
    {
        return MAPPER.valueToTree( value );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        repositoryServiceSupplier = context.getService( RepositoryService.class );
    }
}
