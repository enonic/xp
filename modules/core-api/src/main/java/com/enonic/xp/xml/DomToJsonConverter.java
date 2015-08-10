package com.enonic.xp.xml;

import java.util.List;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Strings;

public final class DomToJsonConverter
{
    public ObjectNode convert( final Element root )
    {
        return convert( DomElement.from( root ) );
    }

    private ObjectNode convert( final DomElement root )
    {
        final ObjectNode json = JsonNodeFactory.instance.objectNode();
        processChildElements( json, root );
        return json;
    }

    private JsonNode processElement( final DomElement root )
    {
        final List<Attr> attributes = root.getAttributes();
        final List<DomElement> children = root.getChildren();
        final String value = root.getValue();

        if ( attributes.isEmpty() && children.isEmpty() )
        {
            if ( Strings.isNullOrEmpty( value ) )
            {
                return JsonNodeFactory.instance.nullNode();
            }

            return JsonNodeFactory.instance.textNode( value );
        }

        final ObjectNode json = JsonNodeFactory.instance.objectNode();
        for ( final Attr attr : root.getAttributes() )
        {
            json.put( "@" + attr.getName(), attr.getValue() );
        }

        processChildElements( json, root );

        if ( !Strings.isNullOrEmpty( value ) )
        {
            json.put( "$value", value );
        }

        return json;
    }

    private void processChildElements( final ObjectNode json, final DomElement parent )
    {
        for ( final DomElement child : parent.getChildren() )
        {
            final String name = child.getTagName();
            final JsonNode jsonChild = processElement( child );
            addToObject( name, json, jsonChild );
        }
    }

    private void addToObject( final String name, final ObjectNode parent, final JsonNode child )
    {
        final JsonNode existing = parent.get( name );
        if ( existing == null )
        {
            parent.set( name, child );
            return;
        }

        if ( existing instanceof ArrayNode )
        {
            ( (ArrayNode) existing ).add( child );
            return;
        }

        final ArrayNode arrayNode = JsonNodeFactory.instance.arrayNode();
        arrayNode.add( existing );
        arrayNode.add( child );
        parent.set( name, arrayNode );
    }
}
