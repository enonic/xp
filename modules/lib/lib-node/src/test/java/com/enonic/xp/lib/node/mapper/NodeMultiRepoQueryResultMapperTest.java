package com.enonic.xp.lib.node.mapper;

import org.junit.Test;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.FindNodesByMultiRepoQueryResult;
import com.enonic.xp.node.MultiRepoNodeHit;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.query.QueryExplanation;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.script.serializer.JsonMapGenerator;

public class NodeMultiRepoQueryResultMapperTest
{

    @Test
    public void name()
        throws Exception
    {

        final FindNodesByMultiRepoQueryResult result = FindNodesByMultiRepoQueryResult.create().
            addNodeHit( MultiRepoNodeHit.create().
                nodeId( NodeId.from( "abc" ) ).
                branch( Branch.from( "fisk" ) ).
                repositoryId( RepositoryId.from( "repo" ) ).
                explanation( QueryExplanation.create().
                    description( "myDescription" ).
                    value( 123L ).
                    addDetail( QueryExplanation.create().
                        description( "myDescription" ).
                        value( 123L ).
                        build() ).
                    build() ).
                build() ).
            build();

        final NodeMultiRepoQueryResultMapper mapper = new NodeMultiRepoQueryResultMapper( result );
        final JsonMapGenerator gen = new JsonMapGenerator();
        mapper.serialize( gen );

        System.out.println( gen.getRoot() );
    }
}