package com.enonic.xp.repo.impl.elasticsearch.query.translator;

import org.junit.Test;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.query.parser.QueryParser;
import com.enonic.xp.repo.impl.StorageSettings;
import com.enonic.xp.repo.impl.elasticsearch.query.ElasticsearchQuery;
import com.enonic.xp.repo.impl.search.SearchRequest;
import com.enonic.xp.repo.impl.search.SearchStorageName;
import com.enonic.xp.repo.impl.search.SearchStorageType;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.PrincipalKeys;

import static org.junit.Assert.*;

public class NodeQueryTranslatorTest
{
    @Test
    public void minimal()
        throws Exception
    {
        final ElasticsearchQuery esQuery = new NodeQueryTranslator().translate( SearchRequest.create().
            query( NodeQuery.create().
                query( QueryParser.parse( "fisk > 'ost'" ) ).
                build() ).
            acl( PrincipalKeys.empty() ).
            settings( StorageSettings.create().
                storageName( SearchStorageName.from( RepositoryId.from( "my-repo" ) ) ).
                storageType( SearchStorageType.from( Branch.from( "myBranch" ) ) ).
                build() ).
            build() );

        assertEquals( "search-my-repo", esQuery.getIndexName() );
        assertEquals( "myBranch", esQuery.getIndexType() );
        assertNotNull( esQuery.getQuery() );
        assertNull( esQuery.getFilter() );
        assertNull( esQuery.getReturnFields() );
    }
}