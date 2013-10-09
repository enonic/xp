package com.enonic.wem.core.data.serializer;

import java.util.Iterator;

import org.jdom.Element;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.data.Data;
import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.type.ValueType;
import com.enonic.wem.api.data.type.ValueTypes;

import static com.enonic.wem.api.data.DataSet.newDataSet;


public final class DataXmlSerializer
{
    public final void generateRootDataSet( final Element dataEl, final RootDataSet rootDataSet )
    {
        for ( final Data data : rootDataSet )
        {
            generateData( dataEl, data );
        }
    }

    private void generateData( final Element parentDataSetEl, final Data data )
    {
        if ( data.isDataSet() )
        {
            generateDataSet( parentDataSetEl, data.toDataSet() );
        }
        else
        {
            generateProperty( parentDataSetEl, data.toProperty() );
        }
    }

    private void generateDataSet( final Element parentDataEl, final DataSet dataSet )
    {
        final String name = dataSet.getPath().getLastElement().getName();
        final Element entryEl = new Element( name ).setAttribute( "type", DataSet.class.getSimpleName() );
        parentDataEl.addContent( entryEl );
        for ( final Data subData : dataSet )
        {
            generateData( entryEl, subData );
        }
    }

    private void generateProperty( final Element parentDataEl, final Property property )
    {
        final String name = property.getPath().getLastElement().getName();

        final Element dataEl = new Element( name ).setAttribute( "type", property.getValueType().getName() );
        parentDataEl.addContent( dataEl );
        dataEl.addContent( property.getString() );
    }

    public final RootDataSet parse( Element parentEl )
    {
        final RootDataSet rootDataSet = new RootDataSet();
        final Iterator<Element> dataIt = parentEl.getChildren().iterator();
        while ( dataIt.hasNext() )
        {
            final Element dataEl = dataIt.next();
            parseData( rootDataSet, dataEl );
        }
        return rootDataSet;
    }

    private void parseData( final DataSet parentDataSet, final Element dataEl )
    {
        final String name = dataEl.getName();
        final String typeAsString = dataEl.getAttributeValue( "type" );
        if ( typeAsString.equals( DataSet.class.getSimpleName() ) )
        {
            final DataSet dataSet = newDataSet().name( name ).build();
            parentDataSet.add( dataSet );
            for ( final Object element : dataEl.getChildren() )
            {
                parseData( dataSet, (Element) element );
            }
        }
        else
        {
            final ValueType type = ValueTypes.parseByName( typeAsString );
            Preconditions.checkNotNull( type, "type was null" );
            parentDataSet.add( type.newProperty( name, dataEl.getText() ) );
        }
    }
}
