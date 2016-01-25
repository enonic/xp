package com.enonic.xp.core.impl.app;

import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.Version;

import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.util.BinaryReference;

import static org.junit.Assert.*;

public class ApplicationNodeTransformerTest
    extends BundleBasedTest
{

    @Test
    public void binary_reference_added()
        throws Exception
    {
        final Application app = Mockito.mock( Application.class );
        Mockito.when( app.getKey() ).thenReturn( ApplicationKey.from( "myApp" ) );
        Mockito.when( app.getVersion() ).thenReturn( Version.valueOf( "1.0.0" ) );
        Mockito.when( app.getMaxSystemVersion() ).thenReturn( "1.0.0" );
        Mockito.when( app.getMinSystemVersion() ).thenReturn( "1.0.0." );
        Mockito.when( app.getDisplayName() ).thenReturn( "displayName" );

        final CreateNodeParams myBundle = ApplicationNodeTransformer.toCreateNodeParams( app, ByteSource.wrap(
            ByteStreams.toByteArray( newBundle( "myBundle", true ).build() ) ) );

        final PropertyTree data = myBundle.getData();

        final BinaryReference binaryReference = data.getBinaryReference( ApplicationNodeTransformer.APPLICATION_BINARY_REF );

        assertNotNull( binaryReference );
    }
}