package com.enonic.wem.query;

import org.junit.Test;

import com.enonic.wem.api.data.Value;
import com.enonic.wem.query.queryfilter.GenericQueryFilter;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.*;

public class GenericQueryFilterTest
{
    @Test
    public void build()
        throws Exception
    {
        final GenericQueryFilter genericQueryFilter = GenericQueryFilter.newQueryFilter().
            add( new Value.String( "test" ), new Value.String( "test2" ) ).
            fieldName( "myField" ).
            build();

        assertNotNull( genericQueryFilter );
        assertEquals( "myField", genericQueryFilter.getFieldName() );
        assertEquals( 2, genericQueryFilter.getValues().size() );
    }
}
