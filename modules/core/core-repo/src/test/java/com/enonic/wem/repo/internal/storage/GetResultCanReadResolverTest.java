package com.enonic.wem.repo.internal.storage;

import java.util.Arrays;

import org.junit.Test;

import com.enonic.wem.repo.internal.ReturnValues;
import com.enonic.wem.repo.internal.index.IndexFieldNameNormalizer;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;

import static org.junit.Assert.*;

public class GetResultCanReadResolverTest
{
    @Test
    public void anonymous_no_access()
        throws Exception
    {
        final ReturnValues returnValues = ReturnValues.create().
            add( IndexFieldNameNormalizer.normalize( NodeIndexPath.PERMISSIONS_READ.toString() ), "user:system:rmy" ).
            build();

        assertFalse(
            GetResultCanReadResolver.canRead( PrincipalKeys.empty(), returnValues.get( NodeIndexPath.PERMISSIONS_READ.getPath() ) ) );
    }

    @Test
    public void anonymous_access()
        throws Exception
    {
        final ReturnValues returnValues = ReturnValues.create().
            add( IndexFieldNameNormalizer.normalize( NodeIndexPath.PERMISSIONS_READ.toString() ), PrincipalKey.ofAnonymous().toString() ).
            build();

        assertTrue(
            GetResultCanReadResolver.canRead( PrincipalKeys.empty(), returnValues.get( NodeIndexPath.PERMISSIONS_READ.toString() ) ) );
    }

    @Test
    public void user_access()
        throws Exception
    {
        final ReturnValues returnValues = ReturnValues.create().
            add( IndexFieldNameNormalizer.normalize( NodeIndexPath.PERMISSIONS_READ.toString() ),
                 PrincipalKey.from( "user:system:rmy" ).toString() ).
            build();
        assertTrue( GetResultCanReadResolver.canRead( PrincipalKeys.from( PrincipalKey.from( "user:system:rmy" ) ),
                                                      returnValues.get( NodeIndexPath.PERMISSIONS_READ.toString() ) ) );
    }

    @Test
    public void group_access()
        throws Exception
    {
        final ReturnValues returnValues = ReturnValues.create().
            add( IndexFieldNameNormalizer.normalize( NodeIndexPath.PERMISSIONS_READ.toString() ),
                 Arrays.asList( "user:system:rmy", "group:system:my-group" ) ).
            build();
        assertTrue( GetResultCanReadResolver.canRead(
            PrincipalKeys.from( PrincipalKey.from( "user:system:rmy" ), PrincipalKey.from( "group:system:my-group" ) ),
            returnValues.get( NodeIndexPath.PERMISSIONS_READ.toString() ) ) );
    }

}