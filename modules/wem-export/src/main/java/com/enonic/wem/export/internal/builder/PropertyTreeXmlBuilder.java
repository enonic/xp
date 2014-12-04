package com.enonic.wem.export.internal.builder;

import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBIntrospector;

import com.enonic.wem.api.data.PropertySet;
import com.enonic.wem.api.data.PropertyTree;
import com.enonic.wem.api.util.GeoPoint;
import com.enonic.wem.api.util.Link;
import com.enonic.wem.api.util.Reference;
import com.enonic.wem.export.internal.xml.XmlBooleanProperty;
import com.enonic.wem.export.internal.xml.XmlDateProperty;
import com.enonic.wem.export.internal.xml.XmlDateTimeProperty;
import com.enonic.wem.export.internal.xml.XmlDoubleProperty;
import com.enonic.wem.export.internal.xml.XmlGeoPointProperty;
import com.enonic.wem.export.internal.xml.XmlHtmlPartProperty;
import com.enonic.wem.export.internal.xml.XmlLinkProperty;
import com.enonic.wem.export.internal.xml.XmlLongProperty;
import com.enonic.wem.export.internal.xml.XmlPropertySet;
import com.enonic.wem.export.internal.xml.XmlPropertyTree;
import com.enonic.wem.export.internal.xml.XmlReferenceProperty;
import com.enonic.wem.export.internal.xml.XmlStringProperty;
import com.enonic.wem.export.internal.xml.XmlTimeProperty;
import com.enonic.wem.export.internal.xml.XmlXmlProperty;
import com.enonic.wem.export.internal.xml.util.XmlDateTimeConverter;
import com.enonic.wem.export.internal.xml.util.XmlStringEscaper;

class PropertyTreeXmlBuilder
{
    public static PropertyTree build( final XmlPropertyTree xmlPropertyTree )
    {
        final PropertyTree propertyTree = new PropertyTree();

        if ( xmlPropertyTree == null )
        {
            return propertyTree;
        }

        final List<JAXBElement<?>> list = xmlPropertyTree.getList();

        doParsePropertyElementList( propertyTree.getRoot(), list );

        return propertyTree;
    }

    private static void doParsePropertyElementList( final PropertySet propertySet, final List<JAXBElement<?>> list )
    {
        for ( final JAXBElement propertyElement : list )
        {
            final Object propertyValue = JAXBIntrospector.getValue( propertyElement );

            if ( propertyValue instanceof XmlPropertySet )
            {
                addPropertySet( (XmlPropertySet) propertyValue, propertySet );
            }
            else if ( propertyValue instanceof XmlStringProperty )
            {
                addStringProperty( (XmlStringProperty) propertyValue, propertySet );
            }
            else if ( propertyValue instanceof XmlBooleanProperty )
            {
                addBooleanProperty( (XmlBooleanProperty) propertyValue, propertySet );
            }
            else if ( propertyValue instanceof XmlLongProperty )
            {
                addLongProperty( (XmlLongProperty) propertyValue, propertySet );
            }
            else if ( propertyValue instanceof XmlDoubleProperty )
            {
                addDoubleProperty( (XmlDoubleProperty) propertyValue, propertySet );
            }
            else if ( propertyValue instanceof XmlXmlProperty )
            {
                addXmlProperty( (XmlXmlProperty) propertyValue, propertySet );
            }
            else if ( propertyValue instanceof XmlHtmlPartProperty )
            {
                addHtmlPartProperty( (XmlHtmlPartProperty) propertyValue, propertySet );
            }
            else if ( propertyValue instanceof XmlDateProperty )
            {
                addDateProperty( (XmlDateProperty) propertyValue, propertySet );
            }
            else if ( propertyValue instanceof XmlDateTimeProperty )
            {
                addDateTimeProperty( (XmlDateTimeProperty) propertyValue, propertySet );
            }
            else if ( propertyValue instanceof XmlTimeProperty )
            {
                addTimeProperty( (XmlTimeProperty) propertyValue, propertySet );
            }
            else if ( propertyValue instanceof XmlReferenceProperty )
            {
                addReferenceProperty( (XmlReferenceProperty) propertyValue, propertySet );
            }
            else if ( propertyValue instanceof XmlLinkProperty )
            {
                addLinkProperty( (XmlLinkProperty) propertyValue, propertySet );
            }
            else if ( propertyValue instanceof XmlGeoPointProperty )
            {
                addGeoPointProperty( (XmlGeoPointProperty) propertyValue, propertySet );
            }
        }
    }

    private static void addPropertySet( final XmlPropertySet propertyValue, final PropertySet propertySet )
    {
        final PropertySet childSet = propertySet.addSet( propertyValue.getName() );

        doParsePropertyElementList( childSet, propertyValue.getList() );
    }

    private static void addStringProperty( final XmlStringProperty xmlProperty, final PropertySet propertySet )
    {
        propertySet.addString( xmlProperty.getName(), xmlProperty.getValue() );
    }

    private static void addXmlProperty( final XmlXmlProperty xmlProperty, final PropertySet propertySet )
    {
        propertySet.addXml( xmlProperty.getName(), XmlStringEscaper.unescapeContent( xmlProperty.getValue() ) );
    }

    private static void addHtmlPartProperty( final XmlHtmlPartProperty xmlProperty, final PropertySet propertySet )
    {
        propertySet.addHtmlPart( xmlProperty.getName(), XmlStringEscaper.unescapeContent( xmlProperty.getValue() ) );
    }

    private static void addBooleanProperty( final XmlBooleanProperty xmlProperty, final PropertySet propertySet )
    {
        propertySet.addBoolean( xmlProperty.getName(), xmlProperty.isValue() );
    }

    private static void addLongProperty( final XmlLongProperty xmlProperty, final PropertySet propertySet )
    {
        propertySet.addLong( xmlProperty.getName(), xmlProperty.getValue() );
    }

    private static void addDoubleProperty( final XmlDoubleProperty xmlProperty, final PropertySet propertySet )
    {
        propertySet.addDouble( xmlProperty.getName(), xmlProperty.getValue() );
    }

    private static void addDateProperty( final XmlDateProperty xmlProperty, final PropertySet propertySet )
    {
        propertySet.addLocalDate( xmlProperty.getName(), XmlDateTimeConverter.toLocalDate( xmlProperty.getValue() ) );
    }

    private static void addDateTimeProperty( final XmlDateTimeProperty xmlProperty, final PropertySet propertySet )
    {
        propertySet.addInstant( xmlProperty.getName(), XmlDateTimeConverter.toInstant( xmlProperty.getValue() ) );
    }

    private static void addTimeProperty( final XmlTimeProperty xmlProperty, final PropertySet propertySet )
    {
        propertySet.addLocalTime( xmlProperty.getName(), XmlDateTimeConverter.toLocalTime( xmlProperty.getValue() ) );
    }

    private static void addReferenceProperty( final XmlReferenceProperty xmlProperty, final PropertySet propertySet )
    {
        propertySet.addReference( xmlProperty.getName(), Reference.from( xmlProperty.getValue() ) );
    }

    private static void addLinkProperty( final XmlLinkProperty xmlProperty, final PropertySet propertySet )
    {
        propertySet.addLink( xmlProperty.getName(), Link.from( xmlProperty.getValue() ) );
    }

    private static void addGeoPointProperty( final XmlGeoPointProperty xmlProperty, final PropertySet propertySet )
    {
        propertySet.addGeoPoint( xmlProperty.getName(), GeoPoint.from( xmlProperty.getValue() ) );
    }
}
