package com.enonic.xp.repo.impl.repository;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.index.IndexType;
import com.enonic.xp.repo.impl.index.IndexServiceInternal;
import com.enonic.xp.repository.Repositories;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SystemConstants;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;

public class Xp8IndexMigrator
{
    private static final Logger LOG = LoggerFactory.getLogger( Xp8IndexMigrator.class );

    private static final IndexResourceProvider DEFAULT_INDEX_RESOURCE_PROVIDER = new DefaultIndexResourceProvider();

    final RepositoryService repositoryService;

    final IndexServiceInternal indexServiceInternal;

    public Xp8IndexMigrator( final RepositoryService repositoryService, final IndexServiceInternal indexServiceInternal )
    {
        this.repositoryService = repositoryService;
        this.indexServiceInternal = indexServiceInternal;
    }

    public void migrate()
    {
        final Repositories repositories = createAdminContext().callWith( repositoryService::list );
        for ( Repository repository : repositories )
        {
            final RepositoryId repositoryId = repository.getId();
            putDefaultMapping( repositoryId, IndexType.VERSION );
            putDefaultMapping( repositoryId, IndexType.BRANCH );
            putDefaultMapping( repositoryId, IndexType.COMMIT );
        }
    }

    private void putDefaultMapping( final RepositoryId repository, final IndexType indexType )
    {
        try
        {
            indexServiceInternal.putIndexMapping( repository, indexType,
                                                  (Map<String, Object>) DEFAULT_INDEX_RESOURCE_PROVIDER.getMapping( indexType )
                                                      .getData()
                                                      .get( indexType.getName() ) );
        }
        catch ( Exception e )
        {
            LOG.error( "cannot migrate index mappings of repository [{}] index [{}]", repository, indexType, e );
        }
    }


    private Context createAdminContext()
    {
        final User admin = User.create().key( PrincipalKey.ofSuperUser() ).login( PrincipalKey.ofSuperUser().getId() ).build();
        final AuthenticationInfo authInfo = AuthenticationInfo.create().principals( RoleKeys.ADMIN ).user( admin ).build();
        return ContextBuilder.create()
            .branch( SystemConstants.BRANCH_SYSTEM )
            .repositoryId( SystemConstants.SYSTEM_REPO_ID )
            .authInfo( authInfo )
            .build();
    }
}
