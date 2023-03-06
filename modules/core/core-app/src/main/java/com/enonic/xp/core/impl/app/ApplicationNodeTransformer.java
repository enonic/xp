package com.enonic.xp.core.impl.app;

import com.google.common.io.ByteSource;

import com.enonic.xp.app.Application;
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

    static CreateNodeParams toCreateNodeParams( final Application app, final ByteSource source )
    {
        final PropertyTree data = createApplicationProperties( app );

        return CreateNodeParams.create().
            parent( ApplicationRepoServiceImpl.APPLICATION_PATH ).
            name( app.getKey().getName() ).
            data( data ).
            attachBinary( BinaryReference.from( APPLICATION_BINARY_REF ), source ).
            refresh( RefreshMode.ALL ).
            build();
    }

    private static PropertyTree createApplicationProperties( final Application app )
    {
        final PropertyTree data = new PropertyTree();
        data.setString( ApplicationPropertyNames.DISPLAY_NAME, app.getDisplayName() );
        data.setString( ApplicationPropertyNames.MAX_SYSTEM_VERSION, app.getMaxSystemVersion() );
        data.setString( ApplicationPropertyNames.MIN_SYSTEM_VERSION, app.getMinSystemVersion() );
        data.setString( ApplicationPropertyNames.VERSION, app.getVersion().toString() );
        data.setString( ApplicationPropertyNames.VENDOR_NAME, app.getVendorName() );
        data.setInstant( ApplicationPropertyNames.MODIFIED_TIME, app.getModifiedTime() );
        data.setBinaryReference( APPLICATION_BINARY_REF, BinaryReference.from( APPLICATION_BINARY_REF ) );
        return data;
    }

    static UpdateNodeParams toUpdateNodeParams( final Application app, final ByteSource source )
    {
        return UpdateNodeParams.create()
            .path( new NodePath( ApplicationRepoServiceImpl.APPLICATION_PATH, NodeName.from( app.getKey().getName() ) ) )
            .attachBinary( BinaryReference.from( APPLICATION_BINARY_REF ), source )
            .editor( node -> node.data = createApplicationProperties( app ) )
            .refresh( RefreshMode.ALL )
            .build();
    }
}
