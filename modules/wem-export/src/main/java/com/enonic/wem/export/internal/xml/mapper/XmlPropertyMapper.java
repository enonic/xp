package com.enonic.wem.export.internal.xml.mapper;

import javax.xml.bind.JAXBElement;

import com.enonic.wem.api.data2.Property;
import com.enonic.wem.api.data2.ValueType;
import com.enonic.wem.api.data2.ValueTypes;
import com.enonic.wem.export.ExportNodeException;
import com.enonic.wem.export.internal.xml.ObjectFactory;

class XmlPropertyMapper
{

    public static JAXBElement toXml( final Property property, ObjectFactory objectFactory )
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

    private static JAXBElement doSerializeProperty( final Property property, final ObjectFactory objectFactory )
    {
        final ValueType type = property.getValue().getType();

        if ( type.equals( ValueTypes.BOOLEAN ) )
        {
            return BooleanPropertyMapper.map( property, objectFactory );
        }
        else if ( type.equals( ValueTypes.STRING ) )
        {
            return StringPropertyMapper.map( property, objectFactory );
        }
        else if ( type.equals( ValueTypes.DOUBLE ) )
        {
            return DoublePropertyMapper.map( property, objectFactory );
        }
        else if ( type.equals( ValueTypes.LONG ) )
        {
            return LongPropertyMapper.map( property, objectFactory );
        }
        else if ( type.equals( ValueTypes.DATE_TIME ) )
        {
            return DateTimePropertyMapper.map( property, objectFactory );
        }
        else if ( type.equals( ValueTypes.LOCAL_DATE_TIME ) )
        {
            return LocalDateTimePropertyMapper.map( property, objectFactory );
        }
        else if ( type.equals( ValueTypes.LOCAL_DATE ) )
        {
            return DatePropertyMapper.map( property, objectFactory );
        }
        else if ( type.equals( ValueTypes.LOCAL_TIME ) )
        {
            return TimePropertyMapper.map( property, objectFactory );
        }
        else if ( type.equals( ValueTypes.GEO_POINT ) )
        {
            return GeoPointPropertyMapper.map( property, objectFactory );
        }
        else if ( type.equals( ValueTypes.HTML_PART ) )
        {
            return HtmlPartPropertyMapper.map( property, objectFactory );
        }
        else if ( type.equals( ValueTypes.XML ) )
        {
            return XmlValuePropertyMapper.map( property, objectFactory );
        }
        else if ( type.equals( ValueTypes.REFERENCE ) )
        {
            return ReferencePropertyMapper.map( property, objectFactory );
        }
        else if ( type.equals( ValueTypes.LINK ) )
        {
            return LinkPropertyMapper.map( property, objectFactory );
        }

        throw new ExportNodeException( "Unknown property-type: " + property.getType() );
    }


}
