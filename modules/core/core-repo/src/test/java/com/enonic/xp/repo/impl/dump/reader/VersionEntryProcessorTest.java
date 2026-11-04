package com.enonic.xp.repo.impl.dump.reader;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.repo.impl.NodeStoreVersion;
import com.enonic.xp.repo.impl.dump.serializer.json.JsonDumpSerializer;
import com.enonic.xp.repo.impl.dump.serializer.json.VersionDumpEntryJson;
import com.enonic.xp.repository.RepositoryId;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.STRING;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class VersionEntryProcessorTest
{
    private static final RepositoryId REPO = RepositoryId.from( "com.enonic.cms.test" );

    @Test
    void counts_each_node_loaded_into_branch_once()
    {
        final VersionEntryProcessor processor = createProcessor();

        for ( int i = 0; i < 3; i++ )
        {
            processor.processLine( versionLine( "node" + i, "/node" + i, List.of( "master", "draft" ) ) );
        }
        processor.processLine( versionLine( "node3", "/node3", List.of( "draft" ) ) );

        assertThat( processor.getBranchLoadResults() ).hasEntrySatisfying( Branch.from( "master" ),
                                                                           r -> assertThat( r.getSuccessful() ).isEqualTo( 3 ) )
            .hasEntrySatisfying( Branch.from( "draft" ), r -> assertThat( r.getSuccessful() ).isEqualTo( 4 ) );
        assertThat( processor.getResult().getSuccessful() ).isEqualTo( 4 );
    }

    @Test
    void loads_devanagari_paths()
    {
        final VersionEntryProcessor processor = createProcessor();

        processor.processLine( versionLine( "node1", "/content/project/privacy-policy/हमें-आपकी-सहमति-चाहिए", List.of( "draft" ) ) );
        processor.processLine( versionLine( "node2", "/content/project/privacy-policy/कुकीज़", List.of( "draft" ) ) );
        processor.processLine( versionLine( "node3", "/content/project/ट्रैकिंग", List.of( "draft" ) ) );
        processor.processLine( versionLine( "node4", "/content/project/ट्रैकिंग/पर्यावरण", List.of( "draft" ) ) );

        assertThat( processor.getResult().getErrors() ).isEmpty();
        assertThat( processor.getResult().getSuccessful() ).isEqualTo( 4 );
        assertThat( processor.getBranchLoadResults() ).hasEntrySatisfying( Branch.from( "draft" ),
                                                                           r -> assertThat( r.getSuccessful() ).isEqualTo( 4 ) );
    }

    @Test
    void invalid_path_is_reported_and_does_not_stop_loading()
    {
        final VersionEntryProcessor processor = createProcessor();

        processor.processLine( versionLine( "node1", "/valid1", List.of( "draft" ) ) );
        processor.processLine( versionLine( "node2", "/inva*lid", List.of( "draft" ) ) );
        processor.processLine( versionLine( "node3", "/valid3", List.of( "draft" ) ) );

        assertThat( processor.getResult().getSuccessful() ).isEqualTo( 2 );
        assertThat( processor.getResult().getErrors() ).singleElement()
            .extracting( EntryLoadError::getMessage, as( STRING ) )
            .contains( "/inva*lid" )
            .contains( "NodeName must not contain '*'" );
        assertThat( processor.getBranchLoadResults() ).hasEntrySatisfying( Branch.from( "draft" ),
                                                                           r -> assertThat( r.getSuccessful() ).isEqualTo( 2 ) );
    }

    private static VersionEntryProcessor createProcessor()
    {
        final DumpReader dumpReader = mock( DumpReader.class );
        when( dumpReader.get( eq( REPO ), any() ) ).thenAnswer(
            _ -> NodeStoreVersion.create().id( NodeId.from( "any" ) ).data( new PropertyTree() ).build() );

        return VersionEntryProcessor.create().dumpReader( dumpReader ).nodeLoader( mock( NodeLoader.class ) ).repositoryId( REPO ).build();
    }

    private static String versionLine( final String nodeId, final String nodePath, final List<String> branches )
    {
        return new String( JsonDumpSerializer.serialize( VersionDumpEntryJson.create()
                                                             .nodeId( nodeId )
                                                             .nodePath( nodePath )
                                                             .timestamp( "2026-01-01T00:00:00Z" )
                                                             .version( "v-" + nodeId )
                                                             .nodeBlobKey( "node-" + nodeId )
                                                             .indexConfigBlobKey( "index-" + nodeId )
                                                             .accessControlBlobKey( "acl-" + nodeId )
                                                             .branches( branches )
                                                             .build() ) );
    }
}
