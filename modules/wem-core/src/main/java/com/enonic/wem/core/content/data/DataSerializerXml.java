package com.enonic.wem.core.content.data;

import java.util.Iterator;

import org.jdom.Element;

import com.google.common.base.Preconditions;

import com.enonic.wem.core.content.datatype.BaseDataType;
import com.enonic.wem.core.content.datatype.DataTypes;

import com.enonic.cms.framework.blob.BlobKey;

public class DataSerializerXml
{
    public Element generate( final Data data )
    {
        final String name = data.getPath().resolveFormItemPath().getLastElement();
        Element el = new Element( "data" );
        el.setAttribute( "name", name );
        if ( data.getDataType() != null )
        {
            el.setAttribute( "type", data.getDataType().getName() );
        }
        if ( data.getValue() != null )
        {
            Element valueEl = new Element( "value" );
            if ( data.getDataType().equals( DataTypes.DATA_SET ) )
            {
                final DataSet dataSet = (DataSet) data.getValue();
                for ( final Data subData : dataSet )
                {
                    valueEl.addContent( generate( subData ) );
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
                valueEl.addContent( String.valueOf( data.getValue() ) );
            }
            el.addContent( valueEl );
        }
        else
        {
            // no element if no value...?
        }

        return el;
    }

    public Data parse( final EntryPath parentPath, final Element dataEl )
    {
        final Data.Builder builder = Data.newData();

        final EntryPath entryPath = new EntryPath( parentPath, dataEl.getAttributeValue( "name" ) );
        builder.path( entryPath );
        final BaseDataType type = (BaseDataType) DataTypes.parseByName( dataEl.getAttributeValue( "type" ) );
        Preconditions.checkNotNull( type, "type was null" );
        builder.type( type );
        if ( type.equals( DataTypes.DATA_SET ) )
        {
            final DataSet dataSet = new DataSet( entryPath );
            builder.value( dataSet );
            final Element valueEl = dataEl.getChild( "value" );
            final Iterator<Element> dataIt = valueEl.getChildren().iterator();
            while ( dataIt.hasNext() )
            {
                final Element el = dataIt.next();
                dataSet.add( parse( entryPath, el ) );
            }
        }
        else
        {
            final String valueAsString = dataEl.getChildText( "value" );
            builder.value( valueAsString );
        }

        return builder.build();
    }
}
