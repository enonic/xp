package com.enonic.wem.api.content.data;


import org.junit.Test;

import com.enonic.wem.api.content.datatype.DataTypes;
import com.enonic.wem.api.content.datatype.InconvertibleException;

import static com.enonic.wem.api.content.data.Data.newData;

public class DataTest
{
    @Test(expected = InconvertibleException.class)
    public void given_invalid_value_when_build_then_exception_is_thrown()
    {
        newData().type( DataTypes.DATE ).value( "2012.31.08" ).build();
    }
}
