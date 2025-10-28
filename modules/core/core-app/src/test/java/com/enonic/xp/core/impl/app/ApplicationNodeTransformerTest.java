package com.enonic.xp.core.impl.app;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.osgi.framework.Version;

import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.BinaryAttachment;
import com.enonic.xp.node.BinaryAttachments;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.util.BinaryReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ApplicationNodeTransformerTest
    extends BundleBasedTest
{
    @Test
    void binary_reference_added()
        throws Exception
    {
        final Application app = Mockito.mock( Application.class );
        Mockito.when( app.getKey() ).thenReturn( ApplicationKey.from( "myApp" ) );
        Mockito.when( app.getVersion() ).thenReturn( Version.valueOf( "1.0.0" ) );
        Mockito.when( app.getMaxSystemVersion() ).thenReturn( "1.0.0" );
        Mockito.when( app.getMinSystemVersion() ).thenReturn( "1.0.0." );
        Mockito.when( app.getDisplayName() ).thenReturn( "displayName" );

        final ByteSource appSource = ByteSource.wrap( ByteStreams.toByteArray( newBundle( "myBundle", true ).build() ) );

        final CreateNodeParams createNodeParams = ApplicationNodeTransformer.toCreateNodeParams( app, appSource );

        final PropertyTree data = createNodeParams.getData();

        final BinaryReference binaryReference = data.getBinaryReference( ApplicationNodeTransformer.APPLICATION_BINARY_REF );

        assertNotNull( binaryReference );

        final BinaryAttachment binaryAttachment = createNodeParams.getBinaryAttachments().get( binaryReference );

        assertEquals( appSource, binaryAttachment.getByteSource() );
    }


    @Test
    void app_binary_updated()
        throws Exception
    {

        final PropertyTree data = new PropertyTree();
        final BinaryReference appReference = BinaryReference.from( ApplicationNodeTransformer.APPLICATION_BINARY_REF );
        data.addBinaryReference( ApplicationNodeTransformer.APPLICATION_BINARY_REF, appReference );

        final Application app = Mockito.mock( Application.class );
        Mockito.when( app.getKey() ).thenReturn( ApplicationKey.from( "myApp" ) );
        Mockito.when( app.getVersion() ).thenReturn( Version.valueOf( "1.0.0" ) );
        Mockito.when( app.getMaxSystemVersion() ).thenReturn( "1.0.0" );
        Mockito.when( app.getMinSystemVersion() ).thenReturn( "1.0.0." );
        Mockito.when( app.getDisplayName() ).thenReturn( "displayName" );

        final ByteSource updatedSource = ByteSource.wrap( ByteStreams.toByteArray( newBundle( "myBundleUpdated", true ).build() ) );
        final UpdateNodeParams updateNodeParams = ApplicationNodeTransformer.toUpdateNodeParams( app, updatedSource );

        final BinaryAttachments binaryAttachments = updateNodeParams.getBinaryAttachments();

        assertEquals( updatedSource, binaryAttachments.get( appReference ).getByteSource() );
    }
}
