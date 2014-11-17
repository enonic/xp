package com.enonic.wem.repo.internal.elasticsearch;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

import com.enonic.wem.api.index.IndexPaths;
import com.enonic.wem.api.security.Group;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.Principals;
import com.enonic.wem.api.security.User;
import com.enonic.wem.repo.internal.index.result.GetResult;
import com.enonic.wem.repo.internal.index.result.SearchResultEntry;
import com.enonic.wem.repo.internal.index.result.SearchResultFieldValue;

import static org.junit.Assert.*;

public class GetResultCanReadResolverTest
{

    @Test
    public void anonymous_no_access()
        throws Exception
    {
        assertFalse( GetResultCanReadResolver.canRead( Principals.empty(), new GetResult( SearchResultEntry.create().
            id( "myId" ).
            addField( IndexPaths.HAS_READ_KEY, SearchResultFieldValue.value( "system:user:rmy" ) ).
            build() ) ) );
    }

    @Test
    public void anonymous_access()
        throws Exception
    {
        assertTrue( GetResultCanReadResolver.canRead( Principals.empty(), new GetResult( SearchResultEntry.create().
            id( "myId" ).
            addField( IndexPaths.HAS_READ_KEY, SearchResultFieldValue.value( PrincipalKey.ofAnonymous().toString() ) ).
            build() ) ) );
    }

    @Test
    public void user_access()
        throws Exception
    {
        assertTrue( GetResultCanReadResolver.canRead( Principals.from( User.create().
            login( "rmy" ).
            key( PrincipalKey.from( "system:user:rmy" ) ).
            build() ), new GetResult( SearchResultEntry.create().
            id( "myId" ).
            addField( IndexPaths.HAS_READ_KEY, SearchResultFieldValue.value( PrincipalKey.from( "system:user:rmy" ) ) ).
            build() ) ) );
    }

    @Test
    public void group_access()
        throws Exception
    {
        final User me = User.create().
            login( "rmy" ).
            displayName( "Me!" ).
            key( PrincipalKey.from( "system:user:rmy" ) ).
            build();

        final Group myGroup = Group.create().
            key( PrincipalKey.from( "system:group:my-group" ) ).
            displayName( "My Group" ).
            build();

        List<Object> hasRead = Lists.newArrayList();
        hasRead.add( PrincipalKey.ofAnonymous().toString() );
        hasRead.add( PrincipalKey.from( "system:user:tsi" ).toString() );

        assertTrue( GetResultCanReadResolver.canRead( Principals.from( me, myGroup ), new GetResult( SearchResultEntry.create().
            id( "myId" ).
            addField( IndexPaths.HAS_READ_KEY, SearchResultFieldValue.values( hasRead ) ).
            build() ) ) );
    }


}