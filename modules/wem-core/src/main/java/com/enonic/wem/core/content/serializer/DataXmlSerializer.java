package com.enonic.wem.core.content.serializer;

import java.util.Iterator;

import org.jdom.Element;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.data.DataSet;
import com.enonic.wem.api.content.data.Property;
import com.enonic.wem.api.content.data.type.BaseValueType;
import com.enonic.wem.api.content.data.type.ValueTypes;

import static com.enonic.wem.api.content.data.DataSet.newDataSet;


public final class DataXmlSerializer
{
    public final void generate( final Element parentDataSetEl, final Data data )
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

    void generateRootDataSet( final Element dataEl, final DataSet dataSet )
    {
        for ( final Data data : dataSet )
        {
            generate( dataEl, data );
        }
    }

    private void generateDataSet( final Element parentDataEl, final DataSet dataSet )
    {
        final String name = dataSet.getPath().getLastElement().getName();
        final Element entryEl = new Element( name ).setAttribute( "type", DataSet.class.getSimpleName() );
        parentDataEl.addContent( entryEl );
        for ( final Data subData : dataSet )
        {
            generate( entryEl, subData );
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
            parseData( parentDataSet, dataEl );
        }
    }

    final void parseData( final DataSet parentDataSet, final Element dataEl )
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
            final BaseValueType type = (BaseValueType) ValueTypes.parseByName( typeAsString );
            Preconditions.checkNotNull( type, "type was null" );
            parentDataSet.add( type.newProperty( name, dataEl.getText() ) );
        }
    }
}
