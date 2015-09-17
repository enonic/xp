package com.enonic.wem.repo.internal.elasticsearch;

import org.junit.Test;

import com.enonic.wem.repo.internal.elasticsearch.query.builder.AclFilterBuilderFactory;
import com.enonic.xp.query.filter.Filter;
import com.enonic.xp.query.filter.ValueFilter;
import com.enonic.xp.security.Group;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.Principals;
import com.enonic.xp.security.User;

import static org.junit.Assert.*;

public class AclFilterBuilderFactoryTest
{
    @Test
    public void anonymous()
        throws Exception
    {
        final Filter filter = AclFilterBuilderFactory.create( PrincipalKeys.empty() );

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
            key( PrincipalKey.from( "user:system:rmy" ) ).
            build(), Group.create().
            key( PrincipalKey.from( "group:system:mygroup" ) ).
            displayName( "My group" ).
            build() );

        final Filter filter = AclFilterBuilderFactory.create( principals.getKeys() );

        assertTrue( filter instanceof ValueFilter );
        ValueFilter valueFilter = (ValueFilter) filter;
        assertEquals( 2, valueFilter.getValues().size() );
    }
}