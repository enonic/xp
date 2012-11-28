package com.enonic.wem.core.content.data;

import java.util.Iterator;

import org.jdom.Element;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.data.DataArray;
import com.enonic.wem.api.content.data.DataSet;
import com.enonic.wem.api.content.data.EntryPath;
import com.enonic.wem.api.content.datatype.BaseDataType;
import com.enonic.wem.api.content.datatype.DataTypes;


final class DataXmlSerializer
{
    final void generate( final Element parentDataEl, final Data data )
    {
        // TODO: instead of resolveFormItemPath, make new method which returns element without index

        if ( data.getDataType().equals( DataTypes.SET ) )
        {
            generateSet( parentDataEl, data );
        }
        else if ( data.getDataType().equals( DataTypes.ARRAY ) )
        {
            generateArray( parentDataEl, data );
        }
        else
        {
            generateValue( parentDataEl, data );
        }
    }

    private void generateSet( final Element parentDataEl, final Data data )
    {
        final String name = data.getPath().resolveFormItemPath().getLastElement();
        final Element dataEl = new Element( name ).setAttribute( "type", data.getDataType().getName() );
        parentDataEl.addContent( dataEl );
        final DataSet set = (DataSet) data.getValue();
        for ( final Data subData : set )
        {
            generate( dataEl, subData );
        }
    }

    private void generateArray( final Element parentDataEl, final Data data )
    {
        final String name = data.getPath().resolveFormItemPath().getLastElement();
        final DataArray array = (DataArray) data.getValue();
        for ( final Data element : array )
        {
            final Element dataEl = new Element( name ).setAttribute( "type", element.getDataType().getName() );
            if ( element.hasDataSetAsValue() )
            {
                generateSet( parentDataEl, element );
            }
            else
            {
                dataEl.setText( element.getString() );
                parentDataEl.addContent( dataEl );
            }
        }
    }

    private void generateValue( final Element parentDataEl, final Data data )
    {
        final String name = data.getPath().resolveFormItemPath().getLastElement();
        final Element dataEl = new Element( name ).setAttribute( "type", data.getDataType().getName() );
        parentDataEl.addContent( dataEl );
        if ( data.getDataType().equals( DataTypes.BLOB ) )
        {
            Preconditions.checkArgument( data.getValue() instanceof BlobKey,
                                         "Data at path [%s] of type BLOB needs to have a BlobKey as value before it is serialized: " +
                                             data.getValue().getClass(), data.getPath() );
        }
        dataEl.addContent( data.getString() );
    }

    final void parse( Element parentEl, final DataSet parentDataSet )
    {
        final Iterator<Element> dataIt = parentEl.getChildren().iterator();
        while ( dataIt.hasNext() )
        {
            final Element dataEl = dataIt.next();
            parse( parentDataSet, dataEl );
        }
    }

    final void parse( final DataSet parentDataSet, final Element dataEl )
    {
        final EntryPath path = new EntryPath( parentDataSet.getPath(), dataEl.getName() );
        final BaseDataType type = (BaseDataType) DataTypes.parseByName( dataEl.getAttributeValue( "type" ) );
        Preconditions.checkNotNull( type, "type was null" );

        if ( type.equals( DataTypes.SET ) )
        {
            final DataSet dataSet = new DataSet( path );
            parentDataSet.add( Data.newData().path( path ).type( type ).value( dataSet ).build() );

            final Iterator<Element> dataIt = dataEl.getChildren().iterator();
            while ( dataIt.hasNext() )
            {
                parse( dataSet, dataIt.next() );
            }
        }
        else
        {
            parentDataSet.add( Data.newData().path( path ).type( type ).value( dataEl.getText() ).build() );
        }
    }
}
