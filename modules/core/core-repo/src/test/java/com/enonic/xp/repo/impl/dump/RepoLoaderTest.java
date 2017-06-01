package com.enonic.xp.repo.impl.dump;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.repo.impl.node.AbstractNodeTest;
import com.enonic.xp.repo.impl.node.NodeHelper;

public class RepoLoaderTest
    extends AbstractNodeTest
{

    @Before
    public void setUp()
        throws Exception
    {
        super.setUp();
        createDefaultRootNode();
    }

    @Test
    public void name()
        throws Exception
    {

        final TestDumpReader reader = new TestDumpReader();

        NodeHelper.runAsAdmin( () -> RepoLoader.create().
            nodeService( this.nodeService ).
            repositoryService( this.repositoryService ).
            reader( reader ).
            includeBinaries( true ).
            includeVersions( true ).
            repositoryId( CTX_DEFAULT.getRepositoryId() ).
            build().
            execute() );



    }
}