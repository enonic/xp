package com.enonic.xp.core.impl.app;

import com.google.common.io.ByteSource;

import com.enonic.xp.app.Application;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.BinaryAttachment;
import com.enonic.xp.node.BinaryAttachments;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.util.BinaryReference;

class ApplicationNodeTransformer
{
    private static final String APPLICATION_BINARY_REF = "applicationFile";

    static CreateNodeParams toNode( final Application app, final ByteSource source )
    {
        final PropertyTree data = new PropertyTree();
        data.setString( ApplicationPropertyNames.DISPLAY_NAME, app.getDisplayName() );
        data.setString( ApplicationPropertyNames.MAX_SYSTEM_VERSION, app.getMaxSystemVersion() );
        data.setString( ApplicationPropertyNames.MIN_SYSTEM_VERSION, app.getMaxSystemVersion() );
        data.setString( ApplicationPropertyNames.VERSION, app.getVersion().toString() );
        data.setString( ApplicationPropertyNames.VENDOR_NAME, app.getVendorName() );
        data.setInstant( ApplicationPropertyNames.MODIFIED_TIME, app.getModifiedTime() );
        addFiles( app, data );

        return CreateNodeParams.create().
            parent( ApplicationRepoServiceImpl.APPLICATION_PATH ).
            name( app.getKey().getName() ).
            data( data ).
            setNodeId( NodeId.from( app.getKey().toString() ) ).
            setBinaryAttachments( BinaryAttachments.create().
                add( new BinaryAttachment( BinaryReference.from( APPLICATION_BINARY_REF ), source ) ).
                build() ).
            build();
    }

    private static void addFiles( final Application app, final PropertyTree data )
    {
        final PropertySet filesSet = data.addSet( ApplicationPropertyNames.FILES );
        for ( final String file : app.getFiles() )
        {
            filesSet.addString( ApplicationPropertyNames.FILE, file );
        }
    }
}
