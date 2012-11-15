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
    final Element generate( final Data data )
    {
        // TODO: instead of resolveComponentPath, make new method which returns element without index
        final String name = data.getPath().resolveComponentPath().getLastElement();
        Element dataEl = new Element( name );
        if ( data.getDataType() != null )
        {
            dataEl.setAttribute( "type", data.getDataType().getName() );
        }
        if ( data.getValue() != null )
        {
            if ( data.getDataType().equals( DataTypes.DATA_SET ) )
            {
                final DataSet set = (DataSet) data.getValue();
                for ( final Data subData : set )
                {
                    dataEl.addContent( generate( subData ) );
                }
            }
            else if ( data.getDataType().equals( DataTypes.DATA_ARRAY ) )
            {
                final DataArray array = (DataArray) data.getValue();
                for ( final Data element : array )
                {
                    dataEl.addContent( generate( element ) );
                }
            }
            else
            {
                if ( data.getDataType().equals( DataTypes.BLOB ) )
                {
                    Preconditions.checkArgument( data.getValue() instanceof BlobKey,
                                                 "Data at path [%s] of type BLOB needs to have a BlobKey as value before it is serialized: " +
                                                     data.getValue().getClass(), data.getPath() );
                }
                dataEl.addContent( String.valueOf( data.getValue() ) );
            }
        }
        else
        {
            // no element if no value...?
        }

        return dataEl;
    }

    final Data parse( final EntryPath parentPath, final Element dataEl )
    {
        final Data.Builder builder = Data.newData();

        final EntryPath entryPath = new EntryPath( parentPath, dataEl.getName() );
        builder.path( entryPath );
        final BaseDataType type = (BaseDataType) DataTypes.parseByName( dataEl.getAttributeValue( "type" ) );
        Preconditions.checkNotNull( type, "type was null" );
        builder.type( type );
        if ( type.equals( DataTypes.DATA_SET ) )
        {
            final DataSet dataSet = new DataSet( entryPath );
            builder.value( dataSet );
            final Iterator<Element> dataIt = dataEl.getChildren().iterator();
            while ( dataIt.hasNext() )
            {
                final Element el = dataIt.next();
                dataSet.add( parse( entryPath, el ) );
            }
        }
        else if ( type.equals( DataTypes.DATA_ARRAY ) )
        {
            final DataArray array = new DataArray( entryPath );
            builder.value( array );
            final Iterator<Element> dataIt = dataEl.getChildren().iterator();
            while ( dataIt.hasNext() )
            {
                final Element el = dataIt.next();
                array.add( parse( parentPath, el ) );
            }
        }
        else
        {
            final String valueAsString = dataEl.getText();
            builder.value( valueAsString );
        }

        return builder.build();
    }
}
