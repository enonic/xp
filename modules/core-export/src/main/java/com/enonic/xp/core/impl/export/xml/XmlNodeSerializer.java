package com.enonic.xp.core.impl.export.xml;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.ValueType;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexConfigDocument;
import com.enonic.xp.index.PathIndexConfig;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.node.Node;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.xml.DomBuilder;
import com.enonic.xp.xml.DomHelper;
import com.enonic.xp.xml.schema.SchemaNamespaces;

public final class XmlNodeSerializer
    implements SchemaNamespaces
{
    private final DomBuilder builder;

    private Node node;

    private boolean exportNodeIds;

    public XmlNodeSerializer()
    {
        this.builder = DomBuilder.create( EXPORT_NS, "node" );
    }

    public XmlNodeSerializer node( final Node value )
    {
        this.node = value;
        return this;
    }

    public XmlNodeSerializer exportNodeIds( final boolean value )
    {
        this.exportNodeIds = value;
        return this;
    }

    public String serialize()
    {
        serializeNode();
        return DomHelper.serialize( this.builder.getDocument() );
    }

    private void serializeNode()
    {
        if ( this.exportNodeIds )
        {
            serializeValueElement( "id", this.node.id() );
        }

        serializeValueElement( "childOrder", this.node.getChildOrder() );
        serializeValueElement( "nodeType", this.node.getNodeType() );
        serializeValueElement( "timestamp", this.node.getTimestamp() );

        serialize( this.node.getPermissions() );

        serializeData( this.node.data() );
        serialize( this.node.getIndexConfigDocument() );
    }

    private void serializeData( final PropertyTree value )
    {
        this.builder.start( "data" );
        for ( final Property item : value.getProperties() )
        {
            serializeProperty( item );
        }

        this.builder.end();
    }

    private void serialize( final IndexConfigDocument value )
    {
        this.builder.start( "indexConfigs" );
        serializeValueElement( "analyzer", value.getAnalyzer() );

        if ( value instanceof PatternIndexConfigDocument )
        {
            serialize( (PatternIndexConfigDocument) value );
        }

        this.builder.end();
    }

    private void serialize( final AccessControlList value )
    {
        PermissionsXmlSerializer.create().
            domBuilder( this.builder ).
            accessControlList( value ).
            build().
            serialize();
    }


    private void serialize( final PatternIndexConfigDocument value )
    {
        this.builder.start( "defaultConfig" );
        serialize( value.getDefaultConfig() );
        this.builder.end();

        this.builder.start( "pathIndexConfigs" );
        value.getPathIndexConfigs().forEach( this::serialize );
        this.builder.end();
    }

    private void serialize( final IndexConfig value )
    {
        serializeValueElement( "decideByType", value.isDecideByType() );
        serializeValueElement( "enabled", value.isEnabled() );
        serializeValueElement( "nGram", value.isnGram() );
        serializeValueElement( "fulltext", value.isFulltext() );
        serializeValueElement( "includeInAllText", value.isIncludeInAllText() );
    }

    private void serialize( final PathIndexConfig value )
    {
        this.builder.start( "pathIndexConfig" );

        this.builder.start( "indexConfig" );
        serialize( value.getIndexConfig() );
        this.builder.end();

        serializeValueElement( "path", value.getPath() );
        this.builder.end();
    }

    private void serializeProperty( final Property value )
    {
        final ValueType type = value.getType();
        final String name = value.getName();

        if ( type == ValueTypes.BOOLEAN )
        {
            serializeProperty( "boolean", name, value.getBoolean() );
        }
        else if ( type == ValueTypes.STRING )
        {
            serializeProperty( "string", name, value.getString() );
        }
        else if ( type == ValueTypes.DOUBLE )
        {
            serializeProperty( "double", name, value.getDouble() );
        }
        else if ( type == ValueTypes.LONG )
        {
            serializeProperty( "long", name, value.getLong() );
        }
        else if ( type == ValueTypes.HTML_PART )
        {
            serializeProperty( "htmlPart", name, value.getString() );
        }
        else if ( type == ValueTypes.XML )
        {
            serializeProperty( "xml", name, value.getString() );
        }
        else if ( type == ValueTypes.GEO_POINT )
        {
            serializeProperty( "geoPoint", name, value.getString() );
        }
        else if ( type == ValueTypes.DATE_TIME )
        {
            serializeProperty( "dateTime", name, toStringValue( value.getInstant() ) );
        }
        else if ( type == ValueTypes.LOCAL_DATE_TIME )
        {
            serializeProperty( "localDateTime", name, toStringValue( value.getLocalDateTime() ) );
        }
        else if ( type == ValueTypes.LOCAL_TIME )
        {
            serializeProperty( "localTime", name, toStringValue( value.getLocalTime() ) );
        }
        else if ( type == ValueTypes.LOCAL_DATE )
        {
            serializeProperty( "localDate", name, toStringValue( value.getLocalDate() ) );
        }
        else if ( type == ValueTypes.REFERENCE )
        {
            serializeProperty( "reference", name, value.getReference() );
        }
        else if ( type == ValueTypes.LINK )
        {
            serializeProperty( "link", name, value.getLink() );
        }
        else if ( type == ValueTypes.BINARY_REFERENCE )
        {
            serializeProperty( "binaryReference", name, value.getBinaryReference() );
        }
        else if ( type == ValueTypes.PROPERTY_SET )
        {
            serializePropertySet( value );
        }
        else
        {
            throw new IllegalArgumentException( "Unknown property type [" + type + "]" );
        }
    }

    private void serializeValueElement( final String name, final Object value )
    {
        if ( value != null )
        {
            this.builder.start( name );
            this.builder.text( value.toString() );
            this.builder.end();
        }
    }

    private void serializeProperty( final String tag, final String name, final Object value )
    {
        this.builder.start( tag );
        this.builder.attribute( "name", name );

        if ( value == null )
        {
            this.builder.attribute( "isNull", "true" );
        }
        else
        {
            this.builder.text( value.toString() );
        }

        this.builder.end();
    }

    private String toStringValue( final Instant value )
    {
        if ( value == null )
        {
            return null;
        }

        return XmlDateTimeConverter.format( value );
    }

    private String toStringValue( final LocalDateTime value )
    {
        if ( value == null )
        {
            return null;
        }

        return XmlDateTimeConverter.format( value );
    }

    private String toStringValue( final LocalTime value )
    {
        if ( value == null )
        {
            return null;
        }

        return XmlDateTimeConverter.format( value );
    }

    private String toStringValue( final LocalDate value )
    {
        if ( value == null )
        {
            return null;
        }

        return XmlDateTimeConverter.format( value );
    }

    private void serializePropertySet( final Property value )
    {
        this.builder.start( "property-set" );
        this.builder.attribute( "name", value.getName() );

        final PropertySet data = value.getSet();
        if ( data == null )
        {
            this.builder.attribute( "isNull", "true" );
        }
        else
        {
            serializePropertySet( data );
        }

        this.builder.end();
    }

    private void serializePropertySet( final PropertySet value )
    {
        for ( final Property item : value.getProperties() )
        {
            serializeProperty( item );
        }
    }
}
