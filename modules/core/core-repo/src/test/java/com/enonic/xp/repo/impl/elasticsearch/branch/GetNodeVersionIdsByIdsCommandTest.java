package com.enonic.xp.repo.impl.elasticsearch.branch;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeVersionIds;
import com.enonic.xp.repo.impl.TestContext;
import com.enonic.xp.repo.impl.elasticsearch.ElasticsearchDao;

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
            repository( TestContext.TEST_REPOSITORY.getId() ).
            branch( TestContext.TEST_BRANCH ).
            nodeIds( NodeIds.empty() ).
            elasticsearchDao( this.elasticsearchDao ).
            build().
            execute();

        assertTrue( nodeVersionIds.isEmpty() );

    }
}