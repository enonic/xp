package com.enonic.wem.core.jcr.old;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

public class MockJcrNode
    implements JcrNode
{
    private String id;

    private String name;

    private String nodeType;

    private String path;

    private MockJcrNode parent;

    private final Map<String, Object> properties;

    private final Map<String, List<MockJcrNode>> nodes;

    public MockJcrNode()
    {
        properties = new HashMap<String, Object>();
        nodes = new HashMap<String, List<MockJcrNode>>();
    }

    public MockJcrNode( String name )
    {
        this.name = name;
        properties = new HashMap<String, Object>();
        nodes = new HashMap<String, List<MockJcrNode>>();
    }

    public void setId( final String id )
    {
        this.id = id;
    }

    public void setName( final String name )
    {
        this.name = name;
    }

    public void setNodeType( final String nodeType )
    {
        this.nodeType = nodeType;
    }

    public void setPath( final String path )
    {
        this.path = path;
    }

    public void setParent( final MockJcrNode parent )
    {
        this.parent = parent;
    }

    public void addNode( final MockJcrNode node )
    {
        addNodes( node.getName(), node );
    }

    public void addNodes( final String namePattern, MockJcrNode... nodes )
    {
        List<MockJcrNode> nodeList = this.nodes.get( namePattern );
        if ( nodeList == null )
        {
            nodeList = new ArrayList<MockJcrNode>();
            this.nodes.put( namePattern, nodeList );
        }
        for ( MockJcrNode child : nodes )
        {
            child.setParent( this );
        }
        nodeList.addAll( Arrays.asList( nodes ) );
    }

    @Override
    public String getIdentifier()
    {
        return id;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public boolean isNodeType( final String nodeTypeName )
    {
        return ( nodeType != null ) && nodeType.equals( nodeTypeName );
    }

    @Override
    public String getPath()
    {
        return path;
    }

    @Override
    public JcrNode getParent()
    {
        return parent;
    }

    @Override
    public void addMixin( final String mixinName )
    {

    }

    @Override
    public boolean hasProperty( final String relPath )
    {
        return properties.containsKey( relPath );
    }

    @Override
    public JcrProperty getProperty( final String relPath )
    {
        return null;
    }

    @Override
    public String getPropertyString( final String relPath )
    {
        return (String) properties.get( relPath );
    }

    @Override
    public Boolean getPropertyBoolean( final String relPath )
    {
        return (Boolean) properties.get( relPath );
    }

    @Override
    public Boolean getPropertyBoolean( final String relPath, final boolean defaultValue )
    {
        return properties.containsKey( relPath ) ? (Boolean) properties.get( relPath ) : defaultValue;
    }

    @Override
    public byte[] getPropertyBinary( final String relPath )
    {
        return (byte[]) properties.get( relPath );
    }

    @Override
    public long getPropertyLong( final String relPath )
    {
        return (Long) properties.get( relPath );
    }

    @Override
    public double getPropertyDouble( final String relPath )
    {
        return (Double) properties.get( relPath );
    }

    @Override
    public Date getPropertyDate( final String relPath )
    {
        return (Date) properties.get( relPath );
    }

    @Override
    public DateTime getPropertyDateTime( final String relPath )
    {
        return (DateTime) properties.get( relPath );
    }

    @Override
    public JcrNode getPropertyReference( final String relPath )
    {
        return (JcrNode) properties.get( relPath );
    }

    @Override
    public void setPropertyString( final String relPath, final String value )
    {
        properties.put( relPath, value );
    }

    @Override
    public void setPropertyBoolean( final String relPath, final boolean value )
    {
        properties.put( relPath, value );
    }

    @Override
    public void setPropertyLong( final String relPath, final long value )
    {
        properties.put( relPath, value );
    }

    @Override
    public void setPropertyDouble( final String relPath, final double value )
    {
        properties.put( relPath, value );
    }

    @Override
    public void setPropertyDate( final String relPath, final Date value )
    {
        properties.put( relPath, value );
    }

    @Override
    public void setPropertyDateTime( final String relPath, final DateTime value )
    {
        properties.put( relPath, value );
    }

    @Override
    public void setPropertyBinary( final String relPath, final byte[] value )
    {
        properties.put( relPath, value );
    }

    @Override
    public void setPropertyReference( final String name, final JcrNode value )
    {
        properties.put( name, value );
    }

    @Override
    public JcrNode getNode( final String relPath )
    {
        final List<MockJcrNode> nodeList = nodes.get( relPath );
        return ( nodeList != null ) && ( !nodeList.isEmpty() ) ? nodeList.get( 0 ) : null;
    }

    @Override
    public boolean hasNode( final String relPath )
    {
        return getNode( relPath ) != null;
    }

    @Override
    public JcrNode addNode( final String relPath )
    {
        return addNode( relPath, "nt:unstructured" );
    }

    @Override
    public JcrNode addNode( final String relPath, final String primaryNodeTypeName )
    {
        final MockJcrNode newNode = new MockJcrNode( relPath );
        newNode.setNodeType( primaryNodeTypeName );
        addNodes( relPath, newNode );
        return newNode;
    }

    @Override
    public JcrNodeIterator getNodes( final String namePattern )
    {
        final List<MockJcrNode> nodeList = nodes.get( namePattern );
        if ( nodeList == null )
        {
            return new MockJcrNodeIterator( Collections.<MockJcrNode>emptyList() );
        }
        else
        {
            return new MockJcrNodeIterator( nodeList );
        }
    }

    @Override
    public JcrPropertyIterator getReferences( final String name )
    {
        return null;
    }

    @Override
    public JcrPropertyIterator getReferences()
    {
        return null;
    }

    @Override
    public void remove()
    {

    }
}
