package com.enonic.wem.admin.rest.rpc.relationship;


import java.util.Iterator;
import java.util.Map;

import org.codehaus.jackson.node.ObjectNode;

import com.google.common.collect.Maps;

import com.enonic.wem.admin.json.rpc.JsonRpcContext;
import com.enonic.wem.admin.rest.rpc.AbstractDataRpcHandler;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.relationship.UpdateRelationship;
import com.enonic.wem.api.relationship.RelationshipKey;
import com.enonic.wem.api.relationship.UpdateRelationshipFailureException;
import com.enonic.wem.api.relationship.editor.RelationshipEditors;


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
        final RelationshipKey relationshipKey = RelationshipKey.from( context.param( "relationshipKey" ).required().asObject() );

        final RelationshipEditors.CompositeBuilder compositeEditorBuilder = RelationshipEditors.newCompositeBuilder();
        final ObjectNode addNode = context.param( "add" ).asObject();
        if ( addNode != null )
        {
            final Map<String, String> propertiesToAdd = resolveProperties( addNode );
            compositeEditorBuilder.add( RelationshipEditors.addProperties( propertiesToAdd ) );
        }
        final String[] remove = context.param( "remove" ).asStringArray();
        if ( remove != null && remove.length > 0 )
        {
            compositeEditorBuilder.add( RelationshipEditors.removeProperties( remove ) );
        }

        final UpdateRelationship updateCommand = Commands.relationship().update();
        updateCommand.relationshipKey( relationshipKey );
        updateCommand.editor( compositeEditorBuilder.build() );

        final UpdateRelationshipPropertiesJsonResult.Builder result = UpdateRelationshipPropertiesJsonResult.newBuilder();
        try
        {
            client.execute( updateCommand );
            result.success();
        }
        catch ( UpdateRelationshipFailureException e )
        {
            result.failure( e.firstFailure().reason );
        }
        context.setResult( result.build() );
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
