package com.enonic.wem.core.content.serializer;

import java.util.Iterator;

import org.jdom.Element;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.data.DataSet;
import com.enonic.wem.api.content.data.Entry;
import com.enonic.wem.api.content.data.type.BaseDataType;
import com.enonic.wem.api.content.data.type.DataTypes;


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
            generateData( parentDataSetEl, entry.toData() );
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
        final Element entryEl = new Element( name ).setAttribute( "type", DataTypes.SET.getName() );
        parentDataEl.addContent( entryEl );
        for ( final Entry subEntry : dataSet )
        {
            generate( entryEl, subEntry );
        }
    }

    private void generateData( final Element parentDataEl, final Data data )
    {
        final String name = data.getPath().getLastElement().getName();

        final Element dataEl = new Element( name ).setAttribute( "type", data.getType().getName() );
        parentDataEl.addContent( dataEl );
        dataEl.addContent( data.getString() );
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
        final BaseDataType type = (BaseDataType) DataTypes.parseByName( entryEl.getAttributeValue( "type" ) );
        Preconditions.checkNotNull( type, "type was null" );

        if ( type.equals( DataTypes.SET ) )
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
            parentDataSet.add( type.newData( name, entryEl.getText() ) );
        }
    }
}
