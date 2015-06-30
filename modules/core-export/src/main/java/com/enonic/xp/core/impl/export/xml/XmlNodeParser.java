package com.enonic.xp.core.impl.export.xml;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.apache.commons.lang.StringEscapeUtils;

import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexConfigDocument;
import com.enonic.xp.index.PathIndexConfig;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeType;
import com.enonic.xp.util.BinaryReference;
import com.enonic.xp.util.GeoPoint;
import com.enonic.xp.util.Link;
import com.enonic.xp.util.Reference;
import com.enonic.xp.xml.DomElement;
import com.enonic.xp.xml.parser.XmlObjectParser;
import com.enonic.xp.xml.schema.SchemaNamespaces;

public final class XmlNodeParser
    extends XmlObjectParser<XmlNodeParser>
    implements SchemaNamespaces
{
    private Node.Builder builder;

    public XmlNodeParser builder( final Node.Builder builder )
    {
        this.builder = builder;
        return this;
    }

    @Override
    protected void doParse( final DomElement root )
        throws Exception
    {
        assertTagName( root, "node" );

        final String id = root.getChildValue( "id" );

        if ( id != null )
        {
            this.builder.id( NodeId.from( id ) );
        }

        this.builder.timestamp( root.getChildValue( "timestamp" ) != null ? Instant.parse( root.getChildValue( "timestamp" ) ) : null );

        this.builder.childOrder( ChildOrder.from( root.getChildValue( "childOrder" ) ) );
        this.builder.nodeType( NodeType.from( root.getChildValue( "nodeType" ) ) );

        this.builder.data( parseData( root.getChild( "data" ) ) );
        this.builder.indexConfigDocument( parseIndexConfigs( root.getChild( "indexConfigs" ) ) );

        if ( root.getChild( "permissions" ) != null )
        {
            this.builder.permissions( XmlPermissionsParser.parse( root.getChild( "permissions" ) ) );
        }
    }

    private PropertyTree parseData( final DomElement root )
    {
        final PropertyTree result = new PropertyTree();

        if ( root != null )
        {
            parseData( root, result.getRoot() );
        }

        return result;
    }

    private void parseData( final DomElement root, final PropertySet set )
    {
        for ( final DomElement elem : root.getChildren() )
        {
            parseProperty( elem, set );
        }
    }

    private void parseProperty( final DomElement root, final PropertySet set )
    {
        final String type = root.getTagName();
        final String name = root.getAttribute( "name" );
        final boolean isNull = "true".equals( root.getAttribute( XML_SCHEMA_NS_KEY + ":nil" ) );

        if ( type.equals( "property-set" ) )
        {
            addPropertySet( root, set, name, isNull );
            return;
        }

        final String value = root.getValue();

        switch ( type )
        {
            case "boolean":
                addBooleanProperty( set, name, isNull ? null : value );
                break;
            case "string":
                addStringProperty( set, name, isNull ? null : value );
                break;
            case "double":
                addDoubleProperty( set, name, isNull ? null : value );
                break;
            case "long":
                addLongProperty( set, name, isNull ? null : value );
                break;
            case "htmlPart":
                addHtmlPartProperty( set, name, isNull ? null : value );
                break;
            case "xml":
                addXmlProperty( set, name, isNull ? null : value );
                break;
            case "geoPoint":
                addGeoPointProperty( set, name, isNull ? null : value );
                break;
            case "dateTime":
                addInstantProperty( set, name, isNull ? null : value );
                break;
            case "localTime":
                addLocalTimeProperty( set, name, isNull ? null : value );
                break;
            case "localDate":
                addLocalDateProperty( set, name, isNull ? null : value );
                break;
            case "localDateTime":
                addLocalDateTimeProperty( set, name, isNull ? null : value );
                break;
            case "reference":
                addReferenceProperty( set, name, isNull ? null : value );
                break;
            case "link":
                addLinkProperty( set, name, isNull ? null : value );
                break;
            case "binaryReference":
                addBinaryReferenceProperty( set, name, isNull ? null : value );
                break;
            default:
                throw new IllegalArgumentException( "Unknown property type [" + type + "]" );
        }
    }

    private void addPropertySet( final DomElement root, final PropertySet set, final String name, final boolean isNull )
    {
        if ( isNull )
        {
            set.addSet( name, null );
        }
        else
        {
            parseData( root, set.addSet( name ) );
        }
    }

    private void addBooleanProperty( final PropertySet set, final String name, final String value )
    {
        set.addBoolean( name, value != null ? Boolean.valueOf( value ) : null );
    }

    private void addStringProperty( final PropertySet set, final String name, final String value )
    {
        set.addString( name, xmlDecodeString( value ) );
    }

    private void addDoubleProperty( final PropertySet set, final String name, final String value )
    {
        set.addDouble( name, value != null ? Double.valueOf( value ) : null );
    }

    private void addLongProperty( final PropertySet set, final String name, final String value )
    {
        set.addLong( name, value != null ? Long.valueOf( value ) : null );
    }

    private void addHtmlPartProperty( final PropertySet set, final String name, final String value )
    {
        set.addHtmlPart( name, xmlDecodeString( value ) );
    }

    private void addXmlProperty( final PropertySet set, final String name, final String value )
    {
        set.addXml( name, xmlDecodeString( value ) );
    }

    private String xmlDecodeString( final String value )
    {
        return StringEscapeUtils.unescapeXml( value );
    }

    private void addGeoPointProperty( final PropertySet set, final String name, final String value )
    {
        set.addGeoPoint( name, value != null ? GeoPoint.from( value ) : null );
    }

    private void addInstantProperty( final PropertySet set, final String name, final String value )
    {
        set.addInstant( name, value != null ? parseInstant( value ) : null );
    }

    private void addLocalDateTimeProperty( final PropertySet set, final String name, final String value )
    {
        set.addLocalDateTime( name, value != null ? parseLocalDateTime( value ) : null );
    }

    private void addLocalDateProperty( final PropertySet set, final String name, final String value )
    {
        set.addLocalDate( name, value != null ? parseLocalDate( value ) : null );
    }

    private void addLocalTimeProperty( final PropertySet set, final String name, final String value )
    {
        set.addLocalTime( name, value != null ? parseLocalTime( value ) : null );
    }

    private void addReferenceProperty( final PropertySet set, final String name, final String value )
    {
        set.addReference( name, value != null ? Reference.from( value ) : null );
    }

    private void addLinkProperty( final PropertySet set, final String name, final String value )
    {
        set.addLink( name, value != null ? Link.from( value ) : null );
    }

    private void addBinaryReferenceProperty( final PropertySet set, final String name, final String value )
    {
        set.addBinaryReference( name, value != null ? BinaryReference.from( value ) : null );
    }

    private Instant parseInstant( final String value )
    {
        return XmlDateTimeConverter.parseInstant( value );
    }

    private LocalDateTime parseLocalDateTime( final String value )
    {
        return XmlDateTimeConverter.parseLocalDateTime( value );
    }

    private LocalDate parseLocalDate( final String value )
    {
        return XmlDateTimeConverter.parseLocalDate( value );
    }

    private LocalTime parseLocalTime( final String value )
    {
        return XmlDateTimeConverter.parseLocalTime( value );
    }

    private IndexConfigDocument parseIndexConfigs( final DomElement root )
    {
        final PatternIndexConfigDocument.Builder builder = PatternIndexConfigDocument.create();

        final String analyzer = root.getChildValue( "analyzer" );
        if ( analyzer != null )
        {
            builder.analyzer( analyzer );
        }

        final IndexConfig defaultConfig = parseIndexConfig( root.getChild( "defaultConfig" ) );
        if ( defaultConfig != null )
        {
            builder.defaultConfig( defaultConfig );
        }

        parsePathIndexConfigs( builder, root.getChild( "pathIndexConfigs" ) );
        return builder.build();
    }

    private IndexConfig parseIndexConfig( final DomElement root )
    {
        final IndexConfig.Builder builder = IndexConfig.create();
        builder.decideByType( root.getChildValueAs( "decideByType", Boolean.class, false ) );
        builder.enabled( root.getChildValueAs( "enabled", Boolean.class, false ) );
        builder.fulltext( root.getChildValueAs( "fulltext", Boolean.class, false ) );
        builder.nGram( root.getChildValueAs( "nGram", Boolean.class, false ) );
        builder.includeInAllText( root.getChildValueAs( "includeInAllText", Boolean.class, false ) );
        return builder.build();
    }

    private void parsePathIndexConfigs( final PatternIndexConfigDocument.Builder builder, final DomElement root )
    {
        if ( root == null )
        {
            return;
        }

        for ( final DomElement elem : root.getChildren( "pathIndexConfig" ) )
        {
            builder.addPattern( parsePathIndexConfig( elem ) );
        }
    }

    private PathIndexConfig parsePathIndexConfig( final DomElement root )
    {
        final PathIndexConfig.Builder builder = PathIndexConfig.create();
        builder.path( PropertyPath.from( root.getChildValue( "path" ) ) );
        builder.indexConfig( parseIndexConfig( root.getChild( "indexConfig" ) ) );
        return builder.build();
    }
}
