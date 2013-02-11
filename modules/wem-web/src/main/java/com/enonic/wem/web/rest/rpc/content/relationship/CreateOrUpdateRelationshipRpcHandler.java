package com.enonic.wem.web.rest.rpc.content.relationship;


import java.util.Iterator;

import org.codehaus.jackson.node.ObjectNode;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.relationship.CreateRelationship;
import com.enonic.wem.api.command.content.relationship.UpdateRelationships;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.data.EntryPath;
import com.enonic.wem.api.content.relationship.RelationshipId;
import com.enonic.wem.api.content.relationship.RelationshipIds;
import com.enonic.wem.api.content.relationship.editor.RelationshipEditors;
import com.enonic.wem.api.content.relationshiptype.QualifiedRelationshipTypeName;
import com.enonic.wem.core.content.dao.ContentIdFactory;
import com.enonic.wem.core.content.relationship.dao.RelationshipIdFactory;
import com.enonic.wem.web.json.rpc.JsonRpcContext;
import com.enonic.wem.web.json.rpc.JsonRpcException;
import com.enonic.wem.web.rest.rpc.AbstractDataRpcHandler;

@Component
public final class CreateOrUpdateRelationshipRpcHandler
    extends AbstractDataRpcHandler
{
    public CreateOrUpdateRelationshipRpcHandler()
    {
        super( "relationship_createOrUpdate" );
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final RelationshipId relationshipIdToUpdate = RelationshipIdFactory.from( context.param( "relationshipId" ).asString() );

        if ( relationshipIdToUpdate == null )
        {
            handleCreate( context );
        }
        else
        {
            handleUpdate( context, relationshipIdToUpdate );
        }
    }

    private void handleUpdate( final JsonRpcContext context, final RelationshipId relationshipIdToUpdate )
        throws JsonRpcException
    {
        final UpdateRelationships updateCommand = Commands.relationship().update();
        updateCommand.relationshipIds( RelationshipIds.from( relationshipIdToUpdate ) );

        RelationshipEditors.SetBuilder setPropertiesBuilder = RelationshipEditors.setProperties();
        parseSetProperties( setPropertiesBuilder, context.param( "properties" ).required().asObject() );
        updateCommand.editor( setPropertiesBuilder.build() );

        client.execute( updateCommand );

        context.setResult( CreateOrUpdateRelationshipJsonResult.updated() );
    }

    private void handleCreate( final JsonRpcContext context )
        throws JsonRpcException
    {
        final QualifiedRelationshipTypeName type = QualifiedRelationshipTypeName.from( context.param( "type" ).required().asString() );
        final ContentId fromContent = ContentIdFactory.from( context.param( "fromContent" ).required().asString() );
        final ContentId toContent = ContentIdFactory.from( context.param( "toContent" ).required().asString() );

        final EntryPath managingData;
        if ( context.hasParam( "managingData" ) )
        {
            managingData = EntryPath.from( context.param( "managingData" ).asString() );
        }
        else
        {
            managingData = null;
        }

        final CreateRelationship createCommand = Commands.relationship().create();
        createCommand.type( type );
        createCommand.fromContent( fromContent );
        createCommand.toContent( toContent );
        if ( managingData != null )
        {
            createCommand.managed( managingData );
        }
        final RelationshipId relationshipId = client.execute( createCommand );
        context.setResult( CreateOrUpdateRelationshipJsonResult.created( relationshipId ) );
    }

    private void parseSetProperties( final RelationshipEditors.SetBuilder setPropertiesBuilder, final ObjectNode propertiesNode )
    {
        final Iterator<String> propertyNames = propertiesNode.getFieldNames();
        while ( propertyNames.hasNext() )
        {
            final String propertyKey = propertyNames.next();
            final String propertyValue = propertiesNode.get( propertyKey ).getTextValue();
            setPropertiesBuilder.add( propertyKey, propertyValue );
        }
    }

}
