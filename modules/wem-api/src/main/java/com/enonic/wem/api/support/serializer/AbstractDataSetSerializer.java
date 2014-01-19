package com.enonic.wem.api.support.serializer;

import com.enonic.wem.api.data.DataSet;

public abstract class AbstractDataSetSerializer<TO_DATA_INPUT, FROM_DATA_OUTPUT>
{
    public abstract DataSet toData( final TO_DATA_INPUT in );

    public abstract FROM_DATA_OUTPUT fromData( final DataSet data );

}
