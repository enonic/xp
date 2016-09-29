package com.enonic.xp.lib.node;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.IndexConfigDocument;
import com.enonic.xp.json.JsonToPropertyTreeTranslator;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.script.ScriptValue;

public class CreateNodeParamsFactory
{
    public CreateNodeParamsFactory()
    {
    }

    public CreateNodeParams create( final ScriptValue value )
    {
        final PropertyTree data = new PropertyTree();

        final PropertyTree properties = new JsonToPropertyTreeTranslator().translate( createJson( value.getMap() ) );

        final String name = properties.getString( NodePropertyConstants.NODE_NAME );
        final String parentPath = properties.getString( "_parentPath" );
        final String manualOrderValue = properties.getString( NodePropertyConstants.CHILD_ORDER );
        final String childOrder = properties.getString( NodePropertyConstants.CHILD_ORDER );
        final String nodeState = properties.getString( NodePropertyConstants.NODE_STATE );
        final String nodeType = properties.getString( NodePropertyConstants.NODE_TYPE );
        final PropertySet permissions = properties.getSet( NodePropertyConstants.PERMISSIONS );
        final PropertySet indexConfig = properties.getSet( NodePropertyConstants.INDEX_CONFIG );

        properties.getProperties().forEach( ( property ) -> {
            if ( !property.getName().startsWith( "_" ) )
            {
                handleUserProperty( property, data );
            }
        } );

        return CreateNodeParams.create().
            name( name ).
            parent( Strings.isNullOrEmpty( parentPath ) ? null : NodePath.create( parentPath ).build() ).
            data( data ).
            build();
    }

    private IndexConfigDocument createIndexConfig( final PropertySet propertySet )
    {
        final String analyzer = propertySet.getString( "analyzer" );

        return null;
    }

    private void handleUserProperty( final Property property, final PropertyTree data )
    {
        property.copyTo( data.getRoot() );
    }

    private JsonNode createJson( final Map<?, ?> value )
    {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.valueToTree( value );
    }


}
