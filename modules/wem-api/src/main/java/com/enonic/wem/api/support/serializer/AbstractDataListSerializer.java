package com.enonic.wem.api.support.serializer;

import java.util.List;

import com.enonic.wem.api.data.Data;

public abstract class AbstractDataListSerializer<TO_DATA_INPUT, FROM_DATA_OUTPUT>
{
    public abstract List<Data> toData( final TO_DATA_INPUT in );

    public abstract FROM_DATA_OUTPUT fromData( final List<Data> dataList );

}
