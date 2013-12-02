package com.enonic.wem.query;

import org.junit.Test;

import com.enonic.wem.api.data.Value;
import com.enonic.wem.query.queryfilter.GenericValueQueryFilter;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.*;

public class GenericQueryFilterTest
{
    @Test
    public void build()
        throws Exception
    {
        final GenericValueQueryFilter genericValueQueryFilter = GenericValueQueryFilter.newValueQueryFilter().
            add( new Value.String( "test" ), new Value.String( "test2" ) ).
            fieldName( "myField" ).
            build();

        assertNotNull( genericValueQueryFilter );
        assertEquals( "myField", genericValueQueryFilter.getFieldName() );
        assertEquals( 2, genericValueQueryFilter.getValues().size() );
    }
}
