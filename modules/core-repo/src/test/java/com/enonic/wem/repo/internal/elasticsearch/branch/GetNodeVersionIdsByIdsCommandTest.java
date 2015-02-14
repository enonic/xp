package com.enonic.wem.repo.internal.elasticsearch.branch;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.repo.internal.elasticsearch.ElasticsearchDao;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeVersionIds;

import static com.enonic.wem.repo.internal.TestContext.TEST_REPOSITORY;
import static com.enonic.wem.repo.internal.TestContext.TEST_BRANCH;
import static org.junit.Assert.*;

public class GetNodeVersionIdsByIdsCommandTest
{
    private ElasticsearchDao elasticsearchDao;

    @Before
    public void setUp()
        throws Exception
    {
        elasticsearchDao = Mockito.mock( ElasticsearchDao.class );
    }

    @Test
    public void empty_nodeIds()
        throws Exception
    {
        final NodeVersionIds nodeVersionIds = GetNodeVersionIdsByIdsCommand.create().
            repository( TEST_REPOSITORY.getId() ).
            branch( TEST_BRANCH ).
            nodeIds( NodeIds.empty() ).
            elasticsearchDao( this.elasticsearchDao ).
            build().
            execute();

        assertTrue( nodeVersionIds.isEmpty() );

    }
}