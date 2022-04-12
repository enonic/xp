package com.enonic.xp.core.impl.project.init;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.index.IndexService;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.repository.BranchNotFoundException;
import com.enonic.xp.repository.RepositoryService;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ContentInitializerTest
{
    @Mock
    private NodeService nodeService;

    @Mock
    private RepositoryService repositoryService;

    @Mock(stubOnly = true)
    private IndexService indexService;


    @Test
    public void testBranchNotFound()
    {
        when( repositoryService.isInitialized( Mockito.any() ) ).thenReturn( true );
        when( nodeService.getByPath( Mockito.any() ) ).thenThrow( new BranchNotFoundException( Branch.from( "draft" ) ) );

        final ProjectName projectName = ProjectName.from( "my-project" );

        Assertions.assertFalse( ContentInitializer.create()
                                    .setIndexService( indexService )
                                    .setNodeService( nodeService )
                                    .setRepositoryService( repositoryService )
                                    .repositoryId( projectName.getRepoId() )
                                    .build()
                                    .isInitialized() );
    }
}
