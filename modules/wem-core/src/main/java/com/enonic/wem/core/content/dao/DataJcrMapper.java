package com.enonic.wem.core.content.dao;


import java.math.BigDecimal;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;

import org.joda.time.DateMidnight;

import com.enonic.wem.api.content.data.Data;
import com.enonic.wem.api.content.data.DataSet;
import com.enonic.wem.api.content.data.EntryPath;
import com.enonic.wem.api.content.datatype.DataType;
import com.enonic.wem.api.content.datatype.DataTypes;
import com.enonic.wem.core.jcr.JcrHelper;

class DataJcrMapper
{
    void fromDataToJcr( final Data data, final Node dataNode )
        throws RepositoryException
    {
        dataNode.setProperty( "type", (long) data.getDataType().getKey() );

        if ( data.getDataType() == DataTypes.WHOLE_NUMBER )
        {
            dataNode.setProperty( "value", (Long) data.getValue() );
        }
        else if ( data.getDataType() == DataTypes.DECIMAL_NUMBER )
        {
            dataNode.setProperty( "value", new BigDecimal( (Double) data.getValue() ) );
        }
        else if ( data.getDataType() == DataTypes.XML )
        {
            dataNode.setProperty( "value", data.getString() );
        }
        else if ( data.getDataType() == DataTypes.DATE )
        {
            JcrHelper.setPropertyDateMidnight( dataNode, "value", data.getDate() );
        }
        else if ( data.getDataType() == DataTypes.HTML_PART )
        {
            dataNode.setProperty( "value", data.getString() );
        }
        else if ( data.getDataType() == DataTypes.TEXT )
        {
            dataNode.setProperty( "value", data.getString() );
        }
        else if ( data.getDataType() == DataTypes.GEOGRAPHIC_COORDINATE )
        {
            // TODO: currDataNode.addNode( "value", data.getString() );
        }
        else if ( data.getDataType() == DataTypes.DATA_SET )
        {
            Node valueNode = JcrHelper.getOrAddNode( dataNode, "value" );
            fromDataSetToJcr( data.getDataSet(), valueNode );
        }
    }

    void fromDataSetToJcr( final Iterable<Data> dataIt, final Node parentNode )
        throws RepositoryException
    {
        for ( Data data : dataIt )
        {
            final String dataName = data.getPath().getLastElement().toString();
            final Node dataNode = JcrHelper.getOrAddNode( parentNode, dataName );
            fromDataToJcr( data, dataNode );
        }
    }

    DataSet toDataSet( final NodeIterator dataIt, final EntryPath parentPath )
        throws RepositoryException
    {
        final DataSet dataSet = new DataSet( parentPath );
        while ( dataIt.hasNext() )
        {
            final Node dataNode = dataIt.nextNode();
            dataSet.add( toData( dataNode, parentPath ) );
        }
        return dataSet;
    }

    Data toData( final Node dataNode, final EntryPath parentPath )
        throws RepositoryException
    {
        final EntryPath path = new EntryPath( parentPath, dataNode.getName() );

        final DataType dataType = DataTypes.parseByKey( (int) dataNode.getProperty( "type" ).getLong() );
        if ( dataType == DataTypes.DATA_SET )
        {
            final DataSet value = toDataSet( dataNode.getNode( "value" ).getNodes(), path );
            return Data.newData().type( dataType ).path( path ).value( value ).build();
        }
        else if ( dataType == DataTypes.TEXT )
        {
            final String value = dataNode.getProperty( "value" ).getString();
            return Data.newData().type( dataType ).path( path ).value( value ).build();
        }
        else if ( dataType == DataTypes.XML )
        {
            final String value = dataNode.getProperty( "value" ).getString();
            return Data.newData().type( dataType ).path( path ).value( value ).build();
        }
        else if ( dataType == DataTypes.HTML_PART )
        {
            final String value = dataNode.getProperty( "value" ).getString();
            return Data.newData().type( dataType ).path( path ).value( value ).build();
        }
        else if ( dataType == DataTypes.WHOLE_NUMBER )
        {
            final Long value = dataNode.getProperty( "value" ).getLong();
            return Data.newData().type( dataType ).path( path ).value( value ).build();
        }
        else if ( dataType == DataTypes.DECIMAL_NUMBER )
        {
            final double value = dataNode.getProperty( "value" ).getDecimal().doubleValue();
            return Data.newData().type( dataType ).path( path ).value( value ).build();
        }
        else if ( dataType == DataTypes.GEOGRAPHIC_COORDINATE )
        {
            // TODO
            return null;
        }
        else if ( dataType == DataTypes.DATE )
        {
            final DateMidnight value = JcrHelper.getPropertyDateMidnight( dataNode, "value" );
            return Data.newData().type( dataType ).path( path ).value( value ).build();
        }

        throw new IllegalArgumentException( "Uknonwn dataType: " + dataType );
    }
}
