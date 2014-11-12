package com.enonic.wem.core.elasticsearch;

import org.junit.Test;

import com.enonic.wem.api.query.filter.Filter;
import com.enonic.wem.api.query.filter.ValueFilter;
import com.enonic.wem.api.security.Group;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.Principals;
import com.enonic.wem.api.security.User;

import static org.junit.Assert.*;

public class AclFilterBuilderFactoryTest
{
    @Test
    public void anonymous()
        throws Exception
    {
        final Filter filter = AclFilterBuilderFactory.create( Principals.empty() );

        assertTrue( filter instanceof ValueFilter );
        ValueFilter valueFilter = (ValueFilter) filter;
        assertEquals( 1, valueFilter.getValues().size() );
    }

    @Test
    public void testName()
        throws Exception
    {
        final Principals principals = Principals.from( User.create().
            email( "rmy@enonic.com" ).
            login( "rmy" ).
            displayName( "Runar Myklebust" ).
            key( PrincipalKey.from( "system:user:rmy" ) ).
            build(), Group.create().
            key( PrincipalKey.from( "system:group:mygroup" ) ).
            displayName( "My group" ).
            build() );

        final Filter filter = AclFilterBuilderFactory.create( principals );

        assertTrue( filter instanceof ValueFilter );
        ValueFilter valueFilter = (ValueFilter) filter;
        assertEquals( 2, valueFilter.getValues().size() );
    }
}