package com.enonic.wem.api.query;

import org.junit.Test;

import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.query.filter.GenericValueFilter;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.*;

public class GenericQueryFilterTest
{
    @Test
    public void build()
        throws Exception
    {
        final GenericValueFilter genericValueQueryFilter = GenericValueFilter.newValueQueryFilter().
            add( Value.newString( "test" ), Value.newString( "test2" ) ).
            fieldName( "myField" ).
            build();

        assertNotNull( genericValueQueryFilter );
        assertEquals( "myField", genericValueQueryFilter.getFieldName() );
        assertEquals( 2, genericValueQueryFilter.getValues().size() );
    }
}
