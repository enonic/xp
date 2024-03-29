package com.enonic.xp.repo.impl.dump.serializer.json;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.enonic.xp.blob.NodeVersionKey;
import com.enonic.xp.node.NodeCommitId;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.repo.impl.dump.model.BranchDumpEntry;
import com.enonic.xp.repo.impl.dump.model.VersionMeta;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BranchDumpEntryJsonDumpSerializerTest
{
    private final JsonDumpSerializer serializer = new JsonDumpSerializer();

    @Test
    public void equals()
        throws Exception
    {
        final BranchDumpEntry branchDumpEntry = BranchDumpEntry.create().
            meta( VersionMeta.create().
                nodePath( new NodePath( "/fisk/ost" ) ).
                timestamp( Instant.now() ).
                version( NodeVersionId.from( "fisk" ) ).
                nodeVersionKey( NodeVersionKey.from( "fiskKey", "fiskKey2", "fiskKey3" ) ).
                nodeCommitId( NodeCommitId.from( "commitId" ) ).
                build() ).
            setBinaryReferences( List.of( "1" ) ).
            nodeId( NodeId.from( "myOtherId" ) ).
            build();

        final String serializedEntry = new String( serializer.serialize( branchDumpEntry ), StandardCharsets.UTF_8 );

        final BranchDumpEntry newBranchDumpEntry = serializer.toBranchMetaEntry( serializedEntry );

        assertEquals( branchDumpEntry, newBranchDumpEntry );
    }


}
