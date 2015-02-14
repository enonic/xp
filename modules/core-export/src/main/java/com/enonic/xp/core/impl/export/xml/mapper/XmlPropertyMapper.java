package com.enonic.xp.core.impl.export.xml.mapper;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.ValueType;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.export.ExportNodeException;
import com.enonic.xp.core.impl.export.xml.ObjectFactory;

class XmlPropertyMapper
{

    public static Object toXml( final Property property, ObjectFactory objectFactory )
    {
        try
        {
            return doSerializeProperty( property, objectFactory );
        }
        catch ( Exception e )
        {
            throw new ExportNodeException( "Failed to serialize node to xml", e );
        }
    }

    private static Object doSerializeProperty( final Property property, final ObjectFactory objectFactory )
    {
        final ValueType type = property.getValue().getType();

        if ( type.equals( ValueTypes.BOOLEAN ) )
        {
            return BooleanPropertyMapper.map( property );
        }
        else if ( type.equals( ValueTypes.STRING ) )
        {
            return StringPropertyMapper.map( property );
        }
        else if ( type.equals( ValueTypes.DOUBLE ) )
        {
            return DoublePropertyMapper.map( property );
        }
        else if ( type.equals( ValueTypes.LONG ) )
        {
            return LongPropertyMapper.map( property );
        }
        else if ( type.equals( ValueTypes.DATE_TIME ) )
        {
            return DateTimePropertyMapper.map( property );
        }
        else if ( type.equals( ValueTypes.LOCAL_DATE_TIME ) )
        {
            return LocalDateTimePropertyMapper.map( property );
        }
        else if ( type.equals( ValueTypes.LOCAL_DATE ) )
        {
            return DatePropertyMapper.map( property );
        }
        else if ( type.equals( ValueTypes.LOCAL_TIME ) )
        {
            return TimePropertyMapper.map( property );
        }
        else if ( type.equals( ValueTypes.GEO_POINT ) )
        {
            return GeoPointPropertyMapper.map( property );
        }
        else if ( type.equals( ValueTypes.HTML_PART ) )
        {
            return HtmlPartPropertyMapper.map( property );
        }
        else if ( type.equals( ValueTypes.XML ) )
        {
            return XmlValuePropertyMapper.map( property );
        }
        else if ( type.equals( ValueTypes.REFERENCE ) )
        {
            return ReferencePropertyMapper.map( property );
        }
        else if ( type.equals( ValueTypes.LINK ) )
        {
            return LinkPropertyMapper.map( property );
        }
        else if ( type.equals( ValueTypes.BINARY_REFERENCE ) )
        {
            return BinaryReferencePropertyMapper.map( property );
        }

        throw new IllegalArgumentException(
            "Xml mapper for value-type: " + property.getType() + " in property " + property.getPath() + " not implemented" );
    }
}
