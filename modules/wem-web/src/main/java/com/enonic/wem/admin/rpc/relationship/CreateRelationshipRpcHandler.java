package com.enonic.wem.admin.rpc.relationship;


import java.util.Iterator;

import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.admin.jsonrpc.JsonRpcContext;
import com.enonic.wem.admin.rpc.AbstractDataRpcHandler;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.relationship.CreateRelationship;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.relationship.RelationshipKey;
import com.enonic.wem.api.schema.relationship.QualifiedRelationshipTypeName;


public final class CreateRelationshipRpcHandler
    extends AbstractDataRpcHandler
{
    public CreateRelationshipRpcHandler()
    {
        super( "relationship_create" );
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final QualifiedRelationshipTypeName type = QualifiedRelationshipTypeName.from( context.param( "type" ).required().asString() );
        final ContentId fromContent = ContentId.from( context.param( "fromContent" ).required().asString() );
        final ContentId toContent = ContentId.from( context.param( "toContent" ).required().asString() );

        final CreateRelationship createCommand = Commands.relationship().create();
        createCommand.type( type );
        createCommand.fromContent( fromContent );
        createCommand.toContent( toContent );
        parseSetProperties( createCommand, context.param( "properties" ).required().asObject() );
        client.execute( createCommand );
        context.setResult( CreateRelationshipJsonResult.created(
            RelationshipKey.from( createCommand.getType(), createCommand.getFromContent(), createCommand.getToContent() ) ) );
    }

    private void parseSetProperties( final CreateRelationship createCommand, final ObjectNode propertiesNode )
    {
        final Iterator<String> propertyNames = propertiesNode.getFieldNames();
        while ( propertyNames.hasNext() )
        {
            final String propertyKey = propertyNames.next();
            final String propertyValue = propertiesNode.get( propertyKey ).getTextValue();
            createCommand.property( propertyKey, propertyValue );
        }
    }
}
