package com.enonic.wem.admin.rest.resource.jcr;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.DecimalNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.common.base.Strings;

import com.enonic.wem.core.jcr.provider.JcrSessionProvider;


@Path("tools/jcr")
@Produces("application/json")
public class GetNodesResource
{
    private JcrSessionProvider jcrSessionProvider;

    @GET
    public ObjectNode getNodes( @QueryParam("path") @DefaultValue("/") final String path,
                                @QueryParam("depth") @DefaultValue("3") final int depth )
        throws Exception
    {
        final Session session = this.jcrSessionProvider.login();

        try
        {
            return getNodes( session, path, depth );
        }
        finally
        {
            session.logout();
        }
    }

    private ObjectNode getNodes( final Session session, final String path, final int depth )
        throws Exception
    {
        final Node root = session.getRootNode();
        final String relPath = StringUtils.removeStart( Strings.nullToEmpty( path ), "/" );

        final Node baseNode;
        if ( relPath.isEmpty() )
        {
            baseNode = root;
        }
        else
        {
            baseNode = root.getNode( relPath );
        }

        final ObjectNode rootJson = buildTree( baseNode, depth );
        final ArrayNode nodesJson = arrayNode();
        nodesJson.add( rootJson );

        final ObjectNode result = objectNode();
        result.put( "nodes", nodesJson );
        return result;
    }

    @Inject
    public void setJcrSessionProvider( final JcrSessionProvider jcrSessionProvider )
    {
        this.jcrSessionProvider = jcrSessionProvider;
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
        return JsonNodeFactory.instance.objectNode();
    }

    private ArrayNode arrayNode()
    {
        return JsonNodeFactory.instance.arrayNode();
    }
}
