package com.enonic.wem.core.elasticsearch.workspace;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.core.elasticsearch.ElasticsearchDao;
import com.enonic.wem.core.entity.EntityIds;
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
    public void empty_entityIds()
        throws Exception
    {
        final NodeVersionIds nodeVersionIds = GetNodeVersionIdsByIdsCommand.create().
            repository( TEST_REPOSITORY ).
            workspace( TEST_WORKSPACE ).
            entityIds( EntityIds.empty() ).
            elasticsearchDao( this.elasticsearchDao ).
            build().
            execute();

        assertTrue( nodeVersionIds.isEmpty() );

    }
}