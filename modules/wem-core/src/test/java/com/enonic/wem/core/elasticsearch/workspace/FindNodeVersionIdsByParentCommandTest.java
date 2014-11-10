package com.enonic.wem.core.elasticsearch.workspace;

import java.util.Iterator;

import org.elasticsearch.index.query.QueryBuilder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.core.elasticsearch.ElasticsearchDao;
import com.enonic.wem.core.elasticsearch.QueryProperties;
import com.enonic.wem.core.entity.NodePath;
import com.enonic.wem.core.entity.NodeVersionId;
import com.enonic.wem.core.entity.NodeVersionIds;
import com.enonic.wem.core.index.result.SearchResult;
import com.enonic.wem.core.index.result.SearchResultEntries;
import com.enonic.wem.core.index.result.SearchResultEntry;
import com.enonic.wem.core.index.result.SearchResultField;

import static com.enonic.wem.core.TestContext.TEST_REPOSITORY;
import static com.enonic.wem.core.TestContext.TEST_WORKSPACE;
import static com.enonic.wem.core.elasticsearch.xcontent.WorkspaceXContentBuilderFactory.NODE_VERSION_ID_FIELD_NAME;
import static junit.framework.Assert.assertEquals;

public class FindNodeVersionIdsByParentCommandTest
{
    private ElasticsearchDao elasticsearchDao;

    @Before
    public void setUp()
        throws Exception
    {
        this.elasticsearchDao = Mockito.mock( ElasticsearchDao.class );

    }

    @Test
    public void childrenFound()
        throws Exception
    {
        NodePath parentPath = NodePath.ROOT;

        Mockito.when( elasticsearchDao.search( Mockito.isA( QueryProperties.class ), Mockito.isA( QueryBuilder.class ) ) ).
            thenReturn( SearchResult.create().
                results( SearchResultEntries.create().
                    add( SearchResultEntry.create().
                        id( "1" ).
                        score( 1F ).
                        addField( NODE_VERSION_ID_FIELD_NAME, new SearchResultField( NODE_VERSION_ID_FIELD_NAME, "a" ) ).
                        build() ).
                    add( SearchResultEntry.create().
                        id( "2" ).
                        score( 1F ).
                        addField( NODE_VERSION_ID_FIELD_NAME, new SearchResultField( NODE_VERSION_ID_FIELD_NAME, "b" ) ).
                        build() ).
                    build() ).
                build() );

        final NodeVersionIds result = FindNodeVersionIdsByParentCommand.create().
            elasticsearchDao( this.elasticsearchDao ).
            repository( TEST_REPOSITORY.getId() ).
            parentPath( parentPath ).
            workspace( TEST_WORKSPACE ).
            build().
            execute();

        assertEquals( 2, result.getSize() );
        final Iterator<NodeVersionId> iterator = result.iterator();
        assertEquals( NodeVersionId.from( "a" ), iterator.next() );
        assertEquals( NodeVersionId.from( "b" ), iterator.next() );
    }

    @Test
    public void noChildren()
        throws Exception
    {
        NodePath parentPath = NodePath.ROOT;

        Mockito.when( elasticsearchDao.search( Mockito.isA( QueryProperties.class ), Mockito.isA( QueryBuilder.class ) ) ).
            thenReturn( SearchResult.create().
                results( SearchResultEntries.create().
                    build() ).
                build() );

        final NodeVersionIds result = FindNodeVersionIdsByParentCommand.create().
            elasticsearchDao( this.elasticsearchDao ).
            repository( TEST_REPOSITORY.getId() ).
            parentPath( parentPath ).
            workspace( TEST_WORKSPACE ).
            build().
            execute();

        assertEquals( 0, result.getSize() );
    }
}