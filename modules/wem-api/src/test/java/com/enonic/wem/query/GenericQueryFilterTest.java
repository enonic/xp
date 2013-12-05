package com.enonic.wem.query;

import org.junit.Test;

import com.enonic.wem.api.data.Value;
import com.enonic.wem.query.filter.GenericValueFilter;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.*;

public class GenericQueryFilterTest
{
    @Test
    public void build()
        throws Exception
    {
        final GenericValueFilter genericValueQueryFilter = GenericValueFilter.newValueQueryFilter().
            add( new Value.String( "test" ), new Value.String( "test2" ) ).
            fieldName( "myField" ).
            build();

        assertNotNull( genericValueQueryFilter );
        assertEquals( "myField", genericValueQueryFilter.getFieldName() );
        assertEquals( 2, genericValueQueryFilter.getValues().size() );
    }
}
