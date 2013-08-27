package com.enonic.wem.core.content.serializer;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import com.enonic.wem.api.data.Data;
import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.data.Property;
import com.enonic.wem.core.support.serializer.AbstractJsonSerializer;

public final class DataJsonSerializer
    extends AbstractJsonSerializer<Data>
{
    static final String DATA_NAME = "name";

    static final String DATA_VALUE = "value";

    static final String DATA_TYPE = "type";

    static final String DATA_PATH = "path";

    private final DataSetJsonSerializer dataSetSerializer;

    private final PopertyJsonSerializer dataSerializer;

    public DataJsonSerializer( final ObjectMapper objectMapper, final DataSetJsonSerializer dataSetSerializer )
    {
        super( objectMapper );
        this.dataSetSerializer = dataSetSerializer;
        this.dataSerializer = new PopertyJsonSerializer( objectMapper );
    }

    public DataJsonSerializer()
    {
        dataSetSerializer = new DataSetJsonSerializer( objectMapper() );
        dataSerializer = new PopertyJsonSerializer( objectMapper() );
    }

    public final JsonNode serialize( final Data data )
    {
        if ( data instanceof Property )
        {
            final Property property = (Property) data;
            return dataSerializer.serialize( property );
        }
        else if ( data instanceof DataSet )
        {
            final DataSet dataSet = (DataSet) data;
            return dataSetSerializer.serialize( dataSet );
        }
        else
        {
            throw new IllegalArgumentException( "Unknown type of Data: " + data.getClass().getSimpleName() );
        }
    }

    protected final Data parse( final JsonNode entryNode )
    {
        if ( entryNode.get( DATA_VALUE ).isArray() )
        {
            return dataSetSerializer.parseDataSet( entryNode );
        }
        else
        {
            return dataSerializer.parseProperty( entryNode );
        }
    }
}
