package com.enonic.xp.core.impl.app;

import com.google.common.io.ByteSource;

import com.enonic.xp.core.internal.Millis;
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

    // the jar of a codeless application is no bundle: it is not kept, the node carries no binary
    static CreateNodeParams toCreateNodeParams( final AppInfo app, final ByteSource source )
    {
        final CreateNodeParams.Builder params = CreateNodeParams.create()
            .parent( ApplicationRepoServiceImpl.APPLICATIONS_ROOT )
            .name( app.name )
            .data( createApplicationProperties( app ) )
            .refresh( RefreshMode.ALL );
        if ( !app.codeless )
        {
            params.attachBinary( BinaryReference.from( APPLICATION_BINARY_REF ), source );
        }
        return params.build();
    }

    private static PropertyTree createApplicationProperties( final AppInfo app )
    {
        final PropertyTree data = new PropertyTree();
        data.setString( ApplicationPropertyNames.MAX_SYSTEM_VERSION, app.maxSystemVersion );
        data.setString( ApplicationPropertyNames.MIN_SYSTEM_VERSION, app.minSystemVersion );
        data.setString( ApplicationPropertyNames.VERSION, app.version );
        data.setString( ApplicationPropertyNames.VENDOR_NAME, app.vendorName );
        data.setInstant( ApplicationPropertyNames.MODIFIED_TIME, Millis.now() );
        if ( !app.codeless )
        {
            data.setBinaryReference( APPLICATION_BINARY_REF, BinaryReference.from( APPLICATION_BINARY_REF ) );
        }
        return data;
    }

    static UpdateNodeParams toUpdateNodeParams( final AppInfo app, final ByteSource source )
    {
        final UpdateNodeParams.Builder params = UpdateNodeParams.create()
            .path( new NodePath( ApplicationRepoServiceImpl.APPLICATIONS_ROOT, NodeName.from( app.name ) ) )
            .editor( node -> node.data = createApplicationProperties( app ) )
            .refresh( RefreshMode.ALL );
        if ( !app.codeless )
        {
            params.attachBinary( BinaryReference.from( APPLICATION_BINARY_REF ), source );
        }
        return params.build();
    }
}
