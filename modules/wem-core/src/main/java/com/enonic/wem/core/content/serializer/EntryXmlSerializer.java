package com.enonic.wem.core.content.serializer;

import java.util.Iterator;

import org.jdom.Element;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.data.DataSet;
import com.enonic.wem.api.content.data.Entry;
import com.enonic.wem.api.content.data.Property;
import com.enonic.wem.api.content.data.type.BasePropertyType;
import com.enonic.wem.api.content.data.type.PropertyTypes;


public final class EntryXmlSerializer
{
    public final void generate( final Element parentDataSetEl, final Entry entry )
    {
        if ( entry.isDataSet() )
        {
            generateDataSet( parentDataSetEl, entry.toDataSet() );
        }
        else
        {
            generateProperty( parentDataSetEl, entry.toProperty() );
        }
    }

    void generateRootDataSet( final Element dataEl, final DataSet dataSet )
    {
        for ( final Entry entry : dataSet )
        {
            generate( dataEl, entry );
        }
    }

    private void generateDataSet( final Element parentDataEl, final DataSet dataSet )
    {
        final String name = dataSet.getPath().getLastElement().getName();
        final Element entryEl = new Element( name ).setAttribute( "type", PropertyTypes.SET.getName() );
        parentDataEl.addContent( entryEl );
        for ( final Entry subEntry : dataSet )
        {
            generate( entryEl, subEntry );
        }
    }

    private void generateProperty( final Element parentDataEl, final Property property )
    {
        final String name = property.getPath().getLastElement().getName();

        final Element dataEl = new Element( name ).setAttribute( "type", property.getType().getName() );
        parentDataEl.addContent( dataEl );
        dataEl.addContent( property.getString() );
    }

    public final void parse( Element parentEl, final DataSet parentDataSet )
    {
        final Iterator<Element> dataIt = parentEl.getChildren().iterator();
        while ( dataIt.hasNext() )
        {
            final Element dataEl = dataIt.next();
            parseEntry( parentDataSet, dataEl );
        }
    }

    final void parseEntry( final DataSet parentDataSet, final Element entryEl )
    {
        final String name = entryEl.getName();
        final BasePropertyType type = (BasePropertyType) PropertyTypes.parseByName( entryEl.getAttributeValue( "type" ) );
        Preconditions.checkNotNull( type, "type was null" );

        if ( type.equals( PropertyTypes.SET ) )
        {
            final DataSet dataSet = DataSet.newDataSet().name( name ).build();
            parentDataSet.add( dataSet );
            final Iterator<Element> dataIt = entryEl.getChildren().iterator();
            while ( dataIt.hasNext() )
            {
                parseEntry( dataSet, dataIt.next() );
            }
        }
        else
        {
            parentDataSet.add( type.newProperty( name, entryEl.getText() ) );
        }
    }
}
