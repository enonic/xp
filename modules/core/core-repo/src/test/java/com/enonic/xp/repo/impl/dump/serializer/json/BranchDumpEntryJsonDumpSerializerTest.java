package com.enonic.xp.repo.impl.dump.serializer.json;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.core.internal.Millis;
import com.enonic.xp.node.NodeCommitId;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.NodeVersionKey;
import com.enonic.xp.repo.impl.dump.model.BranchDumpEntry;
import com.enonic.xp.repo.impl.dump.model.VersionMeta;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BranchDumpEntryJsonDumpSerializerTest
{
    private final JsonDumpSerializer serializer = new JsonDumpSerializer();

    @Test
    void equals()
    {
        final BranchDumpEntry branchDumpEntry = new BranchDumpEntry( NodeId.from( "myotherid" ), VersionMeta.create()
            .nodePath( new NodePath( "/fisk/ost" ) )
            .timestamp( Millis.now() )
            .version( NodeVersionId.from( "fisk" ) )
            .nodeVersionKey( NodeVersionKey.create()
                                 .nodeBlobKey( BlobKey.from( "fiskKey" ) )
                                 .indexConfigBlobKey( BlobKey.from( "fiskKey2" ) )
                                 .accessControlBlobKey( BlobKey.from( "fiskKey3" ) )
                                 .build() )
            .nodeCommitId( NodeCommitId.from( "commitid" ) )
            .build(), List.of( "1" ) );

        final String serializedEntry = new String( serializer.serialize( branchDumpEntry ), StandardCharsets.UTF_8 );

        final BranchDumpEntry newBranchDumpEntry = serializer.toBranchMetaEntry( serializedEntry );

        assertEquals( branchDumpEntry, newBranchDumpEntry );
    }


}
