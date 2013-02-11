package com.enonic.wem.web.rest.rpc.content.relationship;


import java.util.Iterator;
import java.util.Map;

import org.codehaus.jackson.node.ObjectNode;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.relationship.UpdateRelationships;
import com.enonic.wem.api.command.content.relationship.UpdateRelationshipsResult;
import com.enonic.wem.api.content.relationship.RelationshipId;
import com.enonic.wem.api.content.relationship.RelationshipIds;
import com.enonic.wem.api.content.relationship.editor.RelationshipEditors;
import com.enonic.wem.core.content.relationship.dao.RelationshipIdFactory;
import com.enonic.wem.web.json.rpc.JsonRpcContext;
import com.enonic.wem.web.rest.rpc.AbstractDataRpcHandler;

@Component
public final class UpdateRelationshipPropertiesRpcHandler
    extends AbstractDataRpcHandler
{
    public UpdateRelationshipPropertiesRpcHandler()
    {
        super( "relationship_update_properties" );
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final RelationshipId relationshipIdToUpdate = RelationshipIdFactory.from( context.param( "relationshipId" ).asString() );

        final UpdateRelationships updateCommand = Commands.relationship().update();
        updateCommand.relationshipIds( RelationshipIds.from( relationshipIdToUpdate ) );

        final ObjectNode addNode = context.param( "add" ).asObject();
        final String[] remove = context.param( "remove" ).asStringArray();

        RelationshipEditors.CompositeBuilder compositeEditorBuilder = RelationshipEditors.newCompositeBuilder();
        if ( addNode != null )
        {
            final Map<String, String> propertiesToAdd = resolveProperties( addNode );
            compositeEditorBuilder.add( RelationshipEditors.addProperties( propertiesToAdd ) );
        }
        if ( remove != null && remove.length > 0 )
        {
            compositeEditorBuilder.add( RelationshipEditors.removeProperties( remove ) );
        }

        updateCommand.editor( compositeEditorBuilder.build() );

        UpdateRelationshipsResult result = client.execute( updateCommand );
        // TODO: A more detailed JSON-result should be returned
        context.setResult( CreateOrUpdateRelationshipJsonResult.updated() );

    }

    private Map<String, String> resolveProperties( final ObjectNode addNode )
    {
        final Iterator<String> propertyNames = addNode.getFieldNames();
        final Map<String, String> propertiesToAdd = Maps.newLinkedHashMap();
        while ( propertyNames.hasNext() )
        {
            final String propertyKey = propertyNames.next();
            final String propertyValue = addNode.get( propertyKey ).getTextValue();
            propertiesToAdd.put( propertyKey, propertyValue );
        }
        return propertiesToAdd;
    }


}
