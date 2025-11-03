package com.enonic.xp.core.impl.audit.serializer;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.enonic.xp.audit.AuditLog;
import com.enonic.xp.audit.AuditLogId;
import com.enonic.xp.audit.AuditLogUri;
import com.enonic.xp.audit.AuditLogUris;
import com.enonic.xp.audit.LogAuditLogParams;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.core.impl.audit.AuditLogConstants;
import com.enonic.xp.core.impl.audit.AuditLogPropertyNames;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.security.PrincipalKey;

public class AuditLogSerializer
{

    public static CreateNodeParams.Builder toCreateNodeParams( final LogAuditLogParams auditLogParams )
    {
        List<String> objectUris = auditLogParams.getObjectUris().
            stream().map( AuditLogUri::toString ).
            collect( Collectors.toList() );

        final PropertyTree tree = new PropertyTree();
        final PropertySet data = tree.getRoot();

        data.addString( AuditLogPropertyNames.TYPE, auditLogParams.getType() );
        data.addInstant( AuditLogPropertyNames.TIME, auditLogParams.getTime() );
        data.addString( AuditLogPropertyNames.SOURCE, auditLogParams.getSource() );

        final PrincipalKey userKey = Objects.requireNonNullElseGet( auditLogParams.getUser(),
                                                                    () -> ContextAccessor.current().getAuthInfo().getUser() != null
                                                                        ? ContextAccessor.current().getAuthInfo().getUser().getKey()
                                                                        : PrincipalKey.ofAnonymous() );

        data.addString( AuditLogPropertyNames.USER, userKey.toString() );
        data.addStrings( AuditLogPropertyNames.OBJECTURIS, objectUris );
        data.addSet( AuditLogPropertyNames.DATA, auditLogParams.getData().getRoot().copy( data.getTree() ) );

        return CreateNodeParams.create().
            nodeType( AuditLogConstants.NODE_TYPE ).
            inheritPermissions( true ).
            parent( NodePath.ROOT ).
            data( tree );
    }

    public static AuditLog fromNode( final Node node )
    {
        final PropertySet data = node.data().getRoot();

        final AuditLogUris.Builder objectUris = AuditLogUris.create();
        StreamSupport.stream( data.getStrings( AuditLogPropertyNames.OBJECTURIS ).spliterator(), false ).
            map( AuditLogUri::from ).
            forEach( objectUris::add );

        return AuditLog.create().
            id( AuditLogId.from( node.id() ) ).
            type( data.getString( AuditLogPropertyNames.TYPE ) ).
            time( data.getInstant( AuditLogPropertyNames.TIME ) ).
            source( data.getString( AuditLogPropertyNames.SOURCE ) ).
            user( PrincipalKey.from( data.getString( AuditLogPropertyNames.USER ) ) ).
            objectUris( objectUris.build() ).
            data( data.getSet( AuditLogPropertyNames.DATA ).toTree() ).
            build();
    }
}
