package com.enonic.wem.core.jcr;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Value;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.BooleanNode;
import org.codehaus.jackson.node.DecimalNode;
import org.codehaus.jackson.node.DoubleNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.LongNode;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.node.TextNode;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;

import com.enonic.wem.api.command.jcr.GetNodes;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;

@Component
public class GetNodesHandler
    extends CommandHandler<GetNodes>
{
    private final JsonNodeFactory jsonNodeFactory;

    public GetNodesHandler()
    {
        super( GetNodes.class );
        jsonNodeFactory = JsonNodeFactory.instance;
    }

    @Override
    public void handle( final CommandContext context, final GetNodes command )
        throws Exception
    {
        final Node root = context.getJcrSession().getRootNode();
        final String path = StringUtils.removeStart( Strings.nullToEmpty( command.getPath() ), "/" );
        final Node baseNode;
        if ( path.isEmpty() )
        {
            baseNode = root;
        }
        else
        {
            baseNode = root.getNode( path );
        }
        final ObjectNode rootJson = buildTree( baseNode, command.getDepth() );
        final ArrayNode nodesJson = arrayNode();
        nodesJson.add( rootJson );
        command.setResult( nodesJson );
    }

    private ObjectNode buildTree( final Node parent, final int depth )
        throws RepositoryException
    {
        final ObjectNode result = objectNode();
        result.put( "name", parent.getName() );
        result.put( "type", parent.getPrimaryNodeType().getName() );

        final ObjectNode nodeProperties = objectNode();
        final PropertyIterator propertiesIterator = parent.getProperties();
        while ( propertiesIterator.hasNext() )
        {
            final Property property = propertiesIterator.nextProperty();
            nodeProperties.put( property.getName(), getProperty( property ) );
        }
        result.put( "properties", nodeProperties );
        result.put( "nodesCount", parent.getNodes().getSize() );

        if ( depth == 0 )
        {
            return result;
        }

        final ArrayNode subNodes = arrayNode();
        final NodeIterator childrenIterator = parent.getNodes();
        while ( childrenIterator.hasNext() )
        {
            final Node child = childrenIterator.nextNode();
            subNodes.add( buildTree( child, depth - 1 ) );
        }
        result.put( "nodes", subNodes );

        return result;
    }

    private JsonNode getProperty( final Property property )
        throws RepositoryException
    {
        if ( property.isMultiple() )
        {
            final Value[] values = property.getValues();
            final ArrayNode valueArray = arrayNode();
            for ( Value value : values )
            {
                valueArray.add( getPropertyValue( value ) );
            }
            return valueArray;
        }
        else
        {
            return getPropertyValue( property.getValue() );
        }
    }

    private JsonNode getPropertyValue( final Value value )
        throws RepositoryException
    {
        if ( value.getType() == PropertyType.STRING )
        {
            return new TextNode( value.getString() );
        }
        else if ( value.getType() == PropertyType.DATE )
        {
            return new TextNode( new DateTime( value.getDate() ).toString() );
        }
        else if ( value.getType() == PropertyType.BINARY )
        {
            return new TextNode( "<binary: " + value.getBinary().getSize() + " bytes>" );
        }
        else if ( value.getType() == PropertyType.DOUBLE )
        {
            return new DoubleNode( value.getDouble() );
        }
        else if ( value.getType() == PropertyType.DECIMAL )
        {
            return new DecimalNode( value.getDecimal() );
        }
        else if ( value.getType() == PropertyType.LONG )
        {
            return new LongNode( value.getLong() );
        }
        else if ( value.getType() == PropertyType.BOOLEAN )
        {
            return BooleanNode.valueOf( value.getBoolean() );
        }
        else if ( value.getType() == PropertyType.NAME )
        {
            return new TextNode( value.getString() );
        }
        else if ( value.getType() == PropertyType.PATH )
        {
            return new TextNode( value.getString() );
        }
        else if ( value.getType() == PropertyType.REFERENCE )
        {
            return new TextNode( value.getString() );
        }
        else if ( value.getType() == PropertyType.WEAKREFERENCE )
        {
            return new TextNode( value.getString() );
        }
        else if ( value.getType() == PropertyType.URI )
        {
            return new TextNode( value.getString() );
        }
        else
        {
            return new TextNode( value.toString() );
        }
    }

    private ObjectNode objectNode()
    {
        return jsonNodeFactory.objectNode();
    }

    private ArrayNode arrayNode()
    {
        return jsonNodeFactory.arrayNode();
    }
}
