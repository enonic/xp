package com.enonic.xp.repo.impl.dump.reader;

import java.io.IOException;

import com.google.common.io.LineProcessor;

import com.enonic.xp.node.ImportNodeParams;
import com.enonic.xp.node.ImportNodeResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.repo.impl.branch.storage.NodeFactory;
import com.enonic.xp.repo.impl.dump.model.DumpEntry;
import com.enonic.xp.repo.impl.dump.model.Meta;
import com.enonic.xp.repo.impl.dump.serializer.DumpEntrySerializer;
import com.enonic.xp.repo.impl.dump.serializer.json.DumpEntryJsonSerializer;

public class DumpLineProcessor
    implements LineProcessor<EntryLoadResult>
{
    private EntryLoadResult result;

    private final NodeService nodeService;

    private final DumpReader dumpReader;

    private final DumpEntrySerializer serializer;

    public DumpLineProcessor( final NodeService nodeService, final DumpReader reader )
    {
        this.nodeService = nodeService;
        this.serializer = new DumpEntryJsonSerializer();
        this.dumpReader = reader;
    }

    @Override
    public boolean processLine( final String line )
        throws IOException
    {
        // Deserialize to DumpEntry
        final DumpEntry dumpEntry = this.serializer.deSerialize( line );

        final Meta meta = dumpEntry.getCurrentVersion();

        final NodeVersionId version = meta.getVersion();

        final NodeVersion nodeVersion = this.dumpReader.get( version );

        // TODO: Add state
        final Node node = NodeFactory.create( nodeVersion, NodeBranchEntry.create().
            nodeId( dumpEntry.getNodeId() ).
            nodePath( meta.getNodePath() ).
            nodeVersionId( meta.getVersion() ).
            timestamp( meta.getTimestamp() ).
            nodeState( meta.getNodeState() ).
            build() );

        final ImportNodeResult result = this.nodeService.importNode( ImportNodeParams.create().
            importNode( node ).
            importPermissions( true ).
            build() );

        return true;
    }

    @Override
    public EntryLoadResult getResult()
    {
        return result;
    }
}
