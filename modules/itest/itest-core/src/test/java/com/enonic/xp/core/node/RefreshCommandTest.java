package com.enonic.xp.core.node;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.repo.impl.node.RefreshCommand;
import com.enonic.xp.repository.IndexException;
import com.enonic.xp.repository.RepositoryId;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RefreshCommandTest
    extends AbstractNodeTest
{

    @BeforeEach
    void setUp()
    {
        createDefaultRootNode();
    }

    @Test
    void refresh_non_existing_repository()
    {
        final RepositoryId nonExistingRepoId = RepositoryId.from( "non-existing-repo" );

        ContextBuilder.from( ctxDefault() )
            .repositoryId( nonExistingRepoId )
            .build()
            .runWith( () -> assertThrows( IndexException.class, () -> RefreshCommand.create()
                .indexServiceInternal( indexServiceInternal )
                .refreshMode( RefreshMode.ALL )
                .build()
                .execute() ) );
    }

    @Test
    void refresh_existing_repository()
    {
        assertDoesNotThrow(
            () -> RefreshCommand.create().indexServiceInternal( indexServiceInternal ).refreshMode( RefreshMode.ALL ).build().execute() );
    }
}
