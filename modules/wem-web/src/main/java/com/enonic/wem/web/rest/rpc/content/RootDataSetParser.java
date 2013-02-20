package com.enonic.wem.web.rest.rpc.content;


import java.util.Iterator;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.content.data.DataSet;
import com.enonic.wem.api.content.data.EntryPath;
import com.enonic.wem.api.content.data.RootDataSet;
import com.enonic.wem.api.content.data.Value;
import com.enonic.wem.api.content.datatype.DataTypes;
import com.enonic.wem.api.content.schema.type.ContentType;
import com.enonic.wem.api.content.schema.type.DataTypeFixer;

import static com.enonic.wem.api.content.data.Value.newValue;

final class RootDataSetParser
{
    private ContentType contentType;

    RootDataSetParser( final ContentType contentType )
    {
        this.contentType = contentType;
    }

    RootDataSetParser()
    {
    }

    RootDataSet parse( final ObjectNode data )
    {
        final RootDataSet rootDataSet = DataSet.newRootDataSet();

        final Iterator<String> fieldNames = data.getFieldNames();
        while ( fieldNames.hasNext() )
        {
            final String fieldName = fieldNames.next();
            final EntryPath path = EntryPath.from( fieldName );

            final JsonNode valueNode = data.get( fieldName );

            if ( valueNode.isValueNode() )
            {
                final String fieldValue = valueNode.getTextValue();
                Value value = newValue().type( DataTypes.TEXT ).value( fieldValue ).build();
                rootDataSet.setData( path, value );
            }
        }

        if ( contentType != null )
        {
            new DataTypeFixer( contentType ).fix( rootDataSet );
        }

        return rootDataSet;
    }

    /*private DataSet parseDataSet( final EntryPath path, final JsonNode valueNode, final DataSet parent )
    {
        DataSet dataSet = new DataSet( path.getLastElement().getName(), parent );
        Iterator<String> fieldNames = valueNode.getFieldNames();
        while ( fieldNames.hasNext() )
        {
            final String fieldName = fieldNames.next();
            JsonNode childNode = valueNode.get( fieldName );
            if ( childNode.isValueNode() )
            {
                final String valueAsString = childNode.getTextValue();
                final Data data = Data.newData().name( fieldName ).type( DataTypes.TEXT ).value( valueAsString ).parent( parent ).build();
                dataSet.add( data );
            }
        }
        return dataSet;
    }*/
}
