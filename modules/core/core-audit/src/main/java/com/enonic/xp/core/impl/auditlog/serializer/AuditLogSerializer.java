package com.enonic.xp.core.impl.auditlog.serializer;

import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.auditlog.AuditLog;
import com.enonic.xp.auditlog.AuditLogId;
import com.enonic.xp.auditlog.AuditLogParams;
import com.enonic.xp.core.impl.auditlog.AuditLogPropertyNames;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.security.PrincipalKey;

public class AuditLogSerializer
{
    public static CreateNodeParams.Builder toCreateNodeParams( final AuditLogParams auditLogParams )
    {
        NodeId id = NodeId.from( new AuditLogId() );

        List<String> objectUris = auditLogParams.getObjectUris().stream().map( e -> e.toString() ).collect( Collectors.toList() );

        final PropertyTree tree = new PropertyTree();
        final PropertySet data = tree.getRoot();

        data.addString( AuditLogPropertyNames.TYPE, auditLogParams.getType() );
        data.addInstant( AuditLogPropertyNames.TIME, auditLogParams.getTime() );
        data.addString( AuditLogPropertyNames.SOURCE, auditLogParams.getSource() );
        data.addString( AuditLogPropertyNames.USER, auditLogParams.getUser().toString() );
        data.addString( AuditLogPropertyNames.MESSAGE, auditLogParams.getMessage() );
        data.addStrings( AuditLogPropertyNames.OBJECTURIS, objectUris );
        data.addSet( AuditLogPropertyNames.DATA, auditLogParams.getData().getRoot().copy( data.getTree() ) );

        return CreateNodeParams.create().
            data( tree ).
            setNodeId( id ).
            name( id.toString() );
    }

    public static AuditLog fromNode( final Node node )
    {
        final PropertySet data = node.data().getRoot();

        final Iterator<URI> objectUris = StreamSupport.stream( data.getStrings( AuditLogPropertyNames.OBJECTURIS ).spliterator(), false ).
            map( URI::create ).
            iterator();

        return AuditLog.create().
            id( AuditLogId.from( node.id() ) ).
            type( data.getString( AuditLogPropertyNames.TYPE ) ).
            time( data.getInstant( AuditLogPropertyNames.TIME ) ).
            source( data.getString( AuditLogPropertyNames.SOURCE ) ).
            user( PrincipalKey.from( data.getString( AuditLogPropertyNames.USER ) ) ).
            message( data.getString( AuditLogPropertyNames.MESSAGE ) ).
            objectUris( ImmutableSet.copyOf( objectUris ) ).
            data( data.getSet( AuditLogPropertyNames.DATA ).toTree() ).
            build();
    }
}
