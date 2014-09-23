package com.enonic.wem.core.entity.json;


import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.junit.Test;

import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.data.DataPath;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.entity.Attachment;
import com.enonic.wem.api.entity.Attachments;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodeName;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.NodePropertyIndexConfig;
import com.enonic.wem.api.entity.PropertyIndexConfig;

import static org.junit.Assert.*;

public class NodeJsonSerializerTest
{
    @Test
    public void serialize_deserialize()
        throws Exception
    {
        Instant modifiedDateTime = LocalDateTime.of( 2013, 1, 2, 3, 4, 5 ).toInstant( ZoneOffset.UTC );

        RootDataSet rootDataSet = new RootDataSet();
        rootDataSet.setProperty( DataPath.from( "a.b.c" ), Value.newDouble( 2.0 ) );
        rootDataSet.setProperty( DataPath.from( "b" ), Value.newLocalDate( LocalDate.now() ) );
        rootDataSet.setProperty( DataPath.from( "c" ), Value.newString( "runar" ) );
        // This will not work atm since JodaTime does not equal datetime with different time-zones
        //rootDataSet.setProperty( DataPath.from( "c" ), new Value.DateTime( DateTime.now() ) );

        Node node = Node.newNode().
            id( EntityId.from( "myId" ) ).
            parent( NodePath.ROOT ).
            name( NodeName.from( "my-name" ) ).
            createdTime( Instant.now() ).
            creator( UserKey.from( "test:creator" ) ).
            modifier( UserKey.from( "test:modifier" ).asUser() ).
            modifiedTime( modifiedDateTime ).
            entityIndexConfig( NodePropertyIndexConfig.create().
                analyzer( "myAnalyzer" ).
                addPropertyIndexConfig( "mypath", PropertyIndexConfig.FULL ).
                build() ).
            rootDataSet( rootDataSet ).
            attachments( Attachments.from( Attachment.
                newAttachment().
                name( "attachment" ).
                blobKey( new BlobKey( "1234" ) ).
                mimeType( "mimetype" ).
                build() ) ).
            build();

        final String serializedNode = NodeJsonSerializer.toString( node );

        final Node deSerializedNode = NodeJsonSerializer.toNode( serializedNode );

        assertEquals( node, deSerializedNode );
    }
}
