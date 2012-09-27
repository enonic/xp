package com.enonic.wem.core.jcr.loader;

import java.io.InputStream;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.PropertyType;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFactory;

import org.elasticsearch.common.base.Splitter;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import com.enonic.wem.core.jcr.JcrHelper;

final class JcrXmlLoader
{
    private final Session session;

    private final ValueFactory valueFactory;

    public JcrXmlLoader( final Session session )
        throws Exception
    {
        this.session = session;
        this.valueFactory = this.session.getValueFactory();
    }

    public void importContent( final InputStream in )
        throws Exception
    {
        final SAXBuilder builder = new SAXBuilder();
        final Document doc = builder.build( in );

        final Element root = doc.getRootElement();
        if ( root.getName().equals( "node" ) )
        {
            createNode( this.session.getRootNode(), root );
        }

        this.session.save();
    }

    private void createNode( final Node parent, Element xml )
        throws Exception
    {
        final String name = xml.getAttributeValue( "name" );
        final String type = xml.getAttributeValue( "type", "nt:unstructured" );
        final String mixin = xml.getAttributeValue( "mixin", "" );

        if ( Strings.isNullOrEmpty( name ) )
        {
            return;
        }

        final Node node = JcrHelper.getOrAddNode( parent, name, type );
        setMixin( node, mixin );

        for ( final Object o : xml.getChildren( "property" ) )
        {
            createProperty( node, Element.class.cast( o ) );
        }

        for ( final Object o : xml.getChildren( "node" ) )
        {
            createNode( node, Element.class.cast( o ) );
        }
    }

    private void createProperty( final Node node, Element xml )
        throws Exception
    {
        final String name = xml.getAttributeValue( "name" );
        final String type = xml.getAttributeValue( "type", "string" );
        final String[] values = getValues( xml );

        if ( Strings.isNullOrEmpty( name ) )
        {
            return;
        }

        node.setProperty( name, createValues( values, type ) );
    }

    private String[] getValues( final Element xml )
        throws Exception
    {
        final List<String> values = Lists.newArrayList();
        for ( final Object o : xml.getChildren( "value" ) )
        {
            values.add( Element.class.cast( o ).getValue() );
        }

        return values.toArray( new String[values.size()] );
    }

    private Value[] createValues( final String[] values, final String type )
        throws Exception
    {
        final List<Value> result = Lists.newArrayList();
        for ( final String value : values )
        {
            result.add( createValue( value, type ) );
        }

        return result.toArray( new Value[result.size()] );
    }

    private Value createValue( final String value, final String type )
        throws Exception
    {
        if ( "string".equalsIgnoreCase( type ) )
        {
            return createValue( value, PropertyType.STRING );
        }
        else if ( "boolean".equalsIgnoreCase( type ) )
        {
            return createValue( value, PropertyType.BOOLEAN );
        }
        else if ( "date".equalsIgnoreCase( type ) )
        {
            return createValue( value, PropertyType.DATE );
        }
        else if ( "double".equalsIgnoreCase( type ) )
        {
            return createValue( value, PropertyType.DOUBLE );
        }
        else if ( "long".equalsIgnoreCase( type ) )
        {
            return createValue( value, PropertyType.LONG );
        }
        else if ( "name".equalsIgnoreCase( type ) )
        {
            return createValue( value, PropertyType.NAME );
        }
        else if ( "path".equalsIgnoreCase( type ) )
        {
            return createValue( value, PropertyType.PATH );
        }
        else if ( "ref".equalsIgnoreCase( type ) )
        {
            return createValue( value, PropertyType.REFERENCE );
        }
        else if ( "weakRef".equalsIgnoreCase( type ) )
        {
            return createValue( value, PropertyType.WEAKREFERENCE );
        }
        else
        {
            throw new IllegalArgumentException( "Unsupported property type [" + type + "]" );
        }
    }

    private Value createValue( final String value, final int type )
        throws Exception
    {
        return this.valueFactory.createValue( value, type );
    }

    private void setMixin( final Node node, final String values )
        throws Exception
    {
        for ( final String name : Splitter.on( ',' ).trimResults().omitEmptyStrings().split( values ) )
        {
            node.addMixin( name );
        }
    }
}
