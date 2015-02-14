package com.enonic.xp.core.impl.export.builder;

import java.util.List;

import com.enonic.xp.core.data.PropertySet;
import com.enonic.xp.core.data.PropertyTree;
import com.enonic.xp.core.util.BinaryReference;
import com.enonic.xp.core.util.GeoPoint;
import com.enonic.xp.core.util.Link;
import com.enonic.xp.core.util.Reference;
import com.enonic.xp.core.impl.export.xml.XmlBinaryReferenceProperty;
import com.enonic.xp.core.impl.export.xml.XmlBooleanProperty;
import com.enonic.xp.core.impl.export.xml.XmlDateProperty;
import com.enonic.xp.core.impl.export.xml.XmlDateTimeProperty;
import com.enonic.xp.core.impl.export.xml.XmlDoubleProperty;
import com.enonic.xp.core.impl.export.xml.XmlGeoPointProperty;
import com.enonic.xp.core.impl.export.xml.XmlHtmlPartProperty;
import com.enonic.xp.core.impl.export.xml.XmlLinkProperty;
import com.enonic.xp.core.impl.export.xml.XmlLocalDateTimeProperty;
import com.enonic.xp.core.impl.export.xml.XmlLongProperty;
import com.enonic.xp.core.impl.export.xml.XmlPropertySet;
import com.enonic.xp.core.impl.export.xml.XmlPropertyTree;
import com.enonic.xp.core.impl.export.xml.XmlReferenceProperty;
import com.enonic.xp.core.impl.export.xml.XmlStringProperty;
import com.enonic.xp.core.impl.export.xml.XmlTimeProperty;
import com.enonic.xp.core.impl.export.xml.XmlXmlProperty;
import com.enonic.xp.core.impl.export.xml.util.XmlDateTimeConverter;
import com.enonic.xp.core.impl.export.xml.util.XmlStringEscaper;

public class PropertyTreeXmlBuilder
{
    public static PropertyTree build( final XmlPropertyTree xmlPropertyTree )
    {
        final PropertyTree propertyTree = new PropertyTree();

        if ( xmlPropertyTree == null )
        {
            return propertyTree;
        }

        final List<Object> list = xmlPropertyTree.getList();

        doParsePropertyElementList( propertyTree.getRoot(), list );

        return propertyTree;
    }

    private static void doParsePropertyElementList( final PropertySet propertySet, final List<Object> list )
    {
        for ( final Object propertyValue : list )
        {
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
            else if ( propertyValue instanceof XmlBinaryReferenceProperty )
            {
                addBinaryReferenceProperty( (XmlBinaryReferenceProperty) propertyValue, propertySet );
            }
            else if ( propertyValue instanceof XmlLocalDateTimeProperty )
            {
                addLocalDateTimeProperty( (XmlLocalDateTimeProperty) propertyValue, propertySet );
            }
        }
    }

    private static void addPropertySet( final XmlPropertySet propertyValue, final PropertySet propertySet )
    {
        if ( propertyValue.isIsNull() != null && propertyValue.isIsNull() )
        {
            propertySet.addSet( propertyValue.getName(), null );
        }
        else
        {
            final PropertySet childSet = propertySet.addSet( propertyValue.getName() );
            doParsePropertyElementList( childSet, propertyValue.getList() );
        }
    }

    private static void addStringProperty( final XmlStringProperty xmlProperty, final PropertySet propertySet )
    {
        if ( xmlProperty.isIsNull() != null && xmlProperty.isIsNull() )
        {
            propertySet.addString( xmlProperty.getName(), null );
        }
        else
        {
            propertySet.addString( xmlProperty.getName(), XmlStringEscaper.unescapeContent( xmlProperty.getValue() ) );
        }
    }

    private static void addXmlProperty( final XmlXmlProperty xmlProperty, final PropertySet propertySet )
    {
        if ( xmlProperty.isIsNull() != null && xmlProperty.isIsNull() )
        {
            propertySet.addXml( xmlProperty.getName(), null );
        }
        else
        {
            propertySet.addXml( xmlProperty.getName(), XmlStringEscaper.unescapeContent( xmlProperty.getValue() ) );
        }
    }

    private static void addHtmlPartProperty( final XmlHtmlPartProperty xmlProperty, final PropertySet propertySet )
    {
        if ( xmlProperty.isIsNull() != null && xmlProperty.isIsNull() )
        {
            propertySet.addHtmlPart( xmlProperty.getName(), null );
        }
        else
        {
            propertySet.addHtmlPart( xmlProperty.getName(), XmlStringEscaper.unescapeContent( xmlProperty.getValue() ) );
        }
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
        if ( xmlProperty.isIsNull() != null && xmlProperty.isIsNull() )
        {
            propertySet.addReference( xmlProperty.getName(), null );
        }
        else
        {
            propertySet.addReference( xmlProperty.getName(), Reference.from( xmlProperty.getValue() ) );
        }
    }

    private static void addLinkProperty( final XmlLinkProperty xmlProperty, final PropertySet propertySet )
    {
        if ( xmlProperty.isIsNull() != null && xmlProperty.isIsNull() )
        {
            propertySet.addLink( xmlProperty.getName(), null );
        }
        else
        {
            propertySet.addLink( xmlProperty.getName(), Link.from( XmlStringEscaper.unescapeContent( xmlProperty.getValue() ) ) );
        }
    }

    private static void addGeoPointProperty( final XmlGeoPointProperty xmlProperty, final PropertySet propertySet )
    {
        if ( xmlProperty.isIsNull() != null && xmlProperty.isIsNull() )
        {
            propertySet.addGeoPoint( xmlProperty.getName(), null );
        }
        else
        {
            propertySet.addGeoPoint( xmlProperty.getName(), GeoPoint.from( xmlProperty.getValue() ) );
        }
    }

    private static void addBinaryReferenceProperty( final XmlBinaryReferenceProperty xmlProperty, final PropertySet propertySet )
    {
        propertySet.addBinaryReference( xmlProperty.getName(), BinaryReference.from( xmlProperty.getValue() ) );
    }

    private static void addLocalDateTimeProperty( final XmlLocalDateTimeProperty xmlProperty, final PropertySet propertySet )
    {
        propertySet.addLocalDateTime( xmlProperty.getName(), XmlDateTimeConverter.toLocalDateTime( xmlProperty.getValue() ) );
    }

}
