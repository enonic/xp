package com.enonic.xp.repo.impl.dump.serializer.json;

import java.time.Instant;

import org.junit.Test;

import com.google.common.collect.Lists;

import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeState;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.repo.impl.dump.model.DumpEntry;
import com.enonic.xp.repo.impl.dump.model.Meta;

import static org.junit.Assert.*;

public class DumpEntryJsonSerializerTest
{
    private final DumpEntryJsonSerializer serializer = new DumpEntryJsonSerializer();

    @Test
    public void equals()
        throws Exception
    {
        final DumpEntry dumpEntry = DumpEntry.create().
            addVersion( Meta.create().
                nodeState( NodeState.DEFAULT ).
                nodePath( NodePath.create( "/fisk/ost" ).build() ).
                current( true ).
                timestamp( Instant.now() ).
                version( NodeVersionId.from( "fisk" ) ).
                build() ).
            addVersion( Meta.create().
                nodeState( NodeState.DEFAULT ).
                nodePath( NodePath.create( "/fisk/katt" ).build() ).
                current( false ).
                timestamp( Instant.now() ).
                version( NodeVersionId.from( "katt" ) ).
                build() ).
            setBinaryReferences( Lists.newArrayList( "1" ) ).
            nodeId( NodeId.from( "myOtherId" ) ).
            build();

        final String serializedEntry = serializer.serialize( dumpEntry );

        final DumpEntry newDumpEntry = serializer.deSerialize( serializedEntry );

        assertEquals( dumpEntry, newDumpEntry );
    }


}