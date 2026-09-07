package com.enonic.xp.repo.impl.index;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.exception.ForbiddenAccessException;
import com.enonic.xp.index.IndexType;
import com.enonic.xp.index.UpdateIndexSettingsParams;
import com.enonic.xp.index.UpdateIndexSettingsResult;
import com.enonic.xp.repo.impl.node.dao.NodeVersionService;
import com.enonic.xp.repo.impl.repository.RepositoryEntryService;
import com.enonic.xp.repo.impl.search.NodeSearchService;
import com.enonic.xp.repo.impl.storage.IndexDataService;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class IndexServiceImplUpdateSettingsTest
{
    private static final RepositoryId REPO = RepositoryId.from( "my-repo" );

    private static final String SETTINGS = "{\"index\":{\"number_of_replicas\":\"2\"}}";

    private static final Context ADMIN_CONTEXT = ContextBuilder.create()
        .authInfo( AuthenticationInfo.create().principals( RoleKeys.ADMIN ).user( User.anonymous() ).build() )
        .build();

    private IndexServiceInternal indexServiceInternal;

    private IndexServiceImpl indexService;

    @BeforeEach
    void setUp()
    {
        indexServiceInternal = mock( IndexServiceInternal.class );
        indexService = new IndexServiceImpl( indexServiceInternal, mock( IndexDataService.class ), mock( NodeSearchService.class ),
                                             mock( NodeVersionService.class ), mock( RepositoryEntryService.class ) );
    }

    @Test
    void bothIndicesByDefault()
    {
        final UpdateIndexSettingsResult result = ADMIN_CONTEXT.callWith(
            () -> indexService.updateIndexSettings( UpdateIndexSettingsParams.create().repository( REPO ).settings( SETTINGS ).build() ) );

        verify( indexServiceInternal ).updateIndex( eq( "search-my-repo" ), any() );
        verify( indexServiceInternal ).updateIndex( eq( "storage-my-repo" ), any() );
        verify( indexServiceInternal, never() ).closeIndices( any() );
        assertEquals( 2, result.getUpdatedIndexes().size() );
        assertTrue( result.getUpdatedIndexes().containsAll( List.of( "search-my-repo", "storage-my-repo" ) ) );
    }

    @Test
    void searchIndexOnly()
    {
        final UpdateIndexSettingsResult result = ADMIN_CONTEXT.callWith( () -> indexService.updateIndexSettings(
            UpdateIndexSettingsParams.create().repository( REPO ).indexType( IndexType.SEARCH ).settings( SETTINGS ).build() ) );

        verify( indexServiceInternal ).updateIndex( eq( "search-my-repo" ), any() );
        verify( indexServiceInternal, never() ).updateIndex( eq( "storage-my-repo" ), any() );
        assertEquals( Set.of( "search-my-repo" ), Set.copyOf( result.getUpdatedIndexes() ) );
    }

    @Test
    void storageIndexForAnyStorageType()
    {
        for ( final IndexType type : new IndexType[]{IndexType.VERSION, IndexType.BRANCH, IndexType.COMMIT} )
        {
            ADMIN_CONTEXT.runWith( () -> indexService.updateIndexSettings(
                UpdateIndexSettingsParams.create().repository( REPO ).indexType( type ).settings( SETTINGS ).build() ) );
        }

        verify( indexServiceInternal, times( 3 ) ).updateIndex( eq( "storage-my-repo" ), any() );
        verify( indexServiceInternal, never() ).updateIndex( eq( "search-my-repo" ), any() );
    }

    @Test
    void requireClosedIndexClosesAndReopens()
    {
        ADMIN_CONTEXT.runWith( () -> indexService.updateIndexSettings( UpdateIndexSettingsParams.create()
                                                                           .repository( REPO )
                                                                           .indexType( IndexType.SEARCH )
                                                                           .settings( SETTINGS )
                                                                           .requireClosedIndex( true )
                                                                           .build() ) );

        final InOrder inOrder = inOrder( indexServiceInternal );
        inOrder.verify( indexServiceInternal ).closeIndices( "search-my-repo" );
        inOrder.verify( indexServiceInternal ).updateIndex( eq( "search-my-repo" ), any() );
        inOrder.verify( indexServiceInternal ).openIndices( "search-my-repo" );
    }

    @Test
    void requiresAdmin()
    {
        final UpdateIndexSettingsParams params = UpdateIndexSettingsParams.create().repository( REPO ).settings( SETTINGS ).build();

        assertThrows( ForbiddenAccessException.class, () -> indexService.updateIndexSettings( params ) );
        verifyNoInteractions( indexServiceInternal );
    }
}
