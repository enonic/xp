package com.enonic.xp.core.nodb;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.itest.NoEmbeddedElasticsearchProbe;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.RefreshMode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Phase 4 Gate F (nodb/BUILD-PHASE-4.md): the gate's headline claim, asserted as a test rather
 * than inferred from a green suite.
 * <p>
 * {@link NoEmbeddedElasticsearchProbe} already runs before every test class in both suites, but on
 * its own that only proves "nothing had booted an ES node by the time each class started". This
 * class closes the two gaps that leaves:
 * <ol>
 * <li><b>Real work first.</b> It writes nodes, refreshes and QUERIES them, so the probe is
 * evaluated after the full write → outbox → indexer → refresh → search path has actually run.
 * "No ES node" is only interesting if search demonstrably worked without one.</li>
 * <li><b>The probe is not vacuous.</b> In DEFAULT mode the same probe must report evidence,
 * because an embedded node genuinely is running. A check that can never fire is not a proof, and
 * this is the negative control that shows it fires — the same discipline Gate C's lost-write
 * regression test used.</li>
 * </ol>
 * It also pins the flag inversion: in nodb mode the search backend is IMPLIED, so
 * {@link NodbTestCluster#isSearchEnabled()} must be true without anyone passing
 * {@code -Dxp.itest.opensearch}.
 */
class NodbNoEmbeddedElasticsearchTest
    extends AbstractNodeTest
{
    @Test
    void the_search_path_works_and_no_embedded_elasticsearch_exists()
    {
        createDefaultRootNode();
        createNode( CreateNodeParams.create()
                        .parent( NodePath.ROOT )
                        .name( "gate-f-node" )
                        .data( data( "gateFMarker", "noembeddedelasticsearch" ) )
                        .build() );
        nodeService.refresh( RefreshMode.ALL );

        final FindNodesByQueryResult result = doQuery( "gateFMarker = 'noembeddedelasticsearch'" );
        assertEquals( 1, result.getNodeIds().getSize(), "the query itself must work, or 'no ES node' proves nothing" );

        final List<String> evidence = NoEmbeddedElasticsearchProbe.evidenceOfEmbeddedElasticsearch();

        if ( NodbTestCluster.isEnabled() )
        {
            assertTrue( NodbTestCluster.isSearchEnabled(),
                        "nodb mode must IMPLY the search backend as of Gate F -- no -Dxp.itest.opensearch needed" );
            assertTrue( evidence.isEmpty(), () -> "an embedded Elasticsearch node exists in nodb mode: " + evidence );
        }
        else
        {
            // Negative control: the probe must be capable of firing, or its silence in nodb mode
            // would carry no information.
            assertNotNull( client, "default mode runs on the embedded Elasticsearch client" );
            assertFalse( evidence.isEmpty(),
                         "the probe found NO trace of the embedded Elasticsearch node that default mode is running on -- " +
                             "it cannot detect one, so its silence in nodb mode would prove nothing" );
        }
    }

    private static PropertyTree data( final String key, final String value )
    {
        final PropertyTree data = new PropertyTree();
        data.addString( key, value );
        return data;
    }
}
