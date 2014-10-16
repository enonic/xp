package com.enonic.wem.core.elasticsearch.workspace;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.core.elasticsearch.ElasticsearchDao;
import com.enonic.wem.core.entity.NodeIds;
import com.enonic.wem.core.entity.NodeVersionIds;

import static com.enonic.wem.core.TestContext.TEST_REPOSITORY;
import static com.enonic.wem.core.TestContext.TEST_WORKSPACE;
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
            workspace( TEST_WORKSPACE ).
            nodeIds( NodeIds.empty() ).
            elasticsearchDao( this.elasticsearchDao ).
            build().
            execute();

        assertTrue( nodeVersionIds.isEmpty() );

    }
}