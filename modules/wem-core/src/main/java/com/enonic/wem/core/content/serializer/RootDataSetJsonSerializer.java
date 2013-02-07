package com.enonic.wem.core.content.serializer;


import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;

import com.enonic.wem.api.content.data.DataSet;
import com.enonic.wem.api.content.data.RootDataSet;
import com.enonic.wem.core.support.serializer.AbstractJsonSerializer;

public class RootDataSetJsonSerializer
    extends AbstractJsonSerializer<RootDataSet>
{
    private DataSetJsonSerializer dataSetSerializer;

    public RootDataSetJsonSerializer( final ObjectMapper objectMapper )
    {
        super( objectMapper );
        dataSetSerializer = new DataSetJsonSerializer( objectMapper );
    }

    public RootDataSetJsonSerializer()
    {
        dataSetSerializer = new DataSetJsonSerializer( objectMapper() );
    }

    @Override
    public JsonNode serialize( final RootDataSet dataSet )
    {
        return dataSetSerializer.serializeEntries( dataSet );
    }


    @Override
    public RootDataSet parse( final JsonNode arrayNode )
    {
        final RootDataSet rootDataSet = DataSet.newRootDataSet();
        dataSetSerializer.parseEntries( (ArrayNode) arrayNode, rootDataSet );
        return rootDataSet;
    }
}
