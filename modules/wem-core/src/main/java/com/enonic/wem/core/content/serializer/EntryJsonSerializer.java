package com.enonic.wem.core.content.serializer;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import com.enonic.wem.api.content.data.DataSet;
import com.enonic.wem.api.content.data.Entry;
import com.enonic.wem.api.content.data.Property;
import com.enonic.wem.core.support.serializer.AbstractJsonSerializer;

public final class EntryJsonSerializer
    extends AbstractJsonSerializer<Entry>
{
    static final String ENTRY_NAME = "name";

    static final String ENTRY_VALUE = "value";

    static final String ENTRY_TYPE = "type";

    static final String ENTRY_PATH = "path";

    private final DataSetJsonSerializer dataSetSerializer;

    private final DataJsonSerializer dataSerializer;

    public EntryJsonSerializer( final ObjectMapper objectMapper, final DataSetJsonSerializer dataSetSerializer )
    {
        super( objectMapper );
        this.dataSetSerializer = dataSetSerializer;
        this.dataSerializer = new DataJsonSerializer( objectMapper );
    }

    public EntryJsonSerializer()
    {
        dataSetSerializer = new DataSetJsonSerializer( objectMapper() );
        dataSerializer = new DataJsonSerializer( objectMapper() );
    }

    public final JsonNode serialize( final Entry entry )
    {
        if ( entry instanceof Property )
        {
            final Property property = (Property) entry;
            return dataSerializer.serialize( property );
        }
        else if ( entry instanceof DataSet )
        {
            final DataSet dataSet = (DataSet) entry;
            return dataSetSerializer.serialize( dataSet );
        }
        else
        {
            throw new IllegalArgumentException( "Unknown type of entry: " + entry.getClass().getSimpleName() );
        }
    }

    protected final Entry parse( final JsonNode entryNode )
    {
        if ( entryNode.get( ENTRY_VALUE ).isArray() )
        {
            return dataSetSerializer.parseDataSet( entryNode );
        }
        else
        {
            return dataSerializer.parseData( entryNode );
        }
    }


}
