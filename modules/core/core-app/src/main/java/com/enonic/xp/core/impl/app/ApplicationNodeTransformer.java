package com.enonic.xp.core.impl.app;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import com.google.common.io.ByteSource;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.util.BinaryReference;


class ApplicationNodeTransformer
{
    static final String APPLICATION_BINARY_REF = "applicationFile";

    static CreateNodeParams toCreateNodeParams( final AppInfo app, final ByteSource source )
    {
        final PropertyTree data = createApplicationProperties( app );

        return CreateNodeParams.create()
            .parent( ApplicationRepoServiceImpl.APPLICATION_PATH )
            .name( app.name )
            .data( data )
            .attachBinary( BinaryReference.from( APPLICATION_BINARY_REF ), source )
            .refresh( RefreshMode.ALL )
            .build();
    }

    private static PropertyTree createApplicationProperties( final AppInfo app )
    {
        final PropertyTree data = new PropertyTree();
        data.setString( ApplicationPropertyNames.DISPLAY_NAME, app.displayName );
        data.setString( ApplicationPropertyNames.MAX_SYSTEM_VERSION, app.maxSystemVersion );
        data.setString( ApplicationPropertyNames.MIN_SYSTEM_VERSION, app.minSystemVersion );
        data.setString( ApplicationPropertyNames.VERSION, app.version );
        data.setString( ApplicationPropertyNames.VENDOR_NAME, app.vendorName );
        data.setInstant( ApplicationPropertyNames.MODIFIED_TIME, Instant.now().truncatedTo( ChronoUnit.MILLIS ) );
        data.setBinaryReference( APPLICATION_BINARY_REF, BinaryReference.from( APPLICATION_BINARY_REF ) );
        return data;
    }

    static UpdateNodeParams toUpdateNodeParams( final AppInfo app, final ByteSource source )
    {
        return UpdateNodeParams.create()
            .path( new NodePath( ApplicationRepoServiceImpl.APPLICATION_PATH, NodeName.from( app.name ) ) )
            .attachBinary( BinaryReference.from( APPLICATION_BINARY_REF ), source )
            .editor( node -> node.data = createApplicationProperties( app ) )
            .refresh( RefreshMode.ALL )
            .build();
    }
}
