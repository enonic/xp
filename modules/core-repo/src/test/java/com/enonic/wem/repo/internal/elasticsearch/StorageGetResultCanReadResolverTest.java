package com.enonic.wem.repo.internal.elasticsearch;

import java.util.Arrays;

import org.junit.Test;

import com.enonic.wem.repo.internal.index.IndexFieldNameNormalizer;
import com.enonic.wem.repo.internal.index.result.ResultFieldValues;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;

import static org.junit.Assert.*;

public class StorageGetResultCanReadResolverTest
{

    @Test
    public void anonymous_no_access()
        throws Exception
    {
        final ResultFieldValues resultFieldValues = ResultFieldValues.create().
            add( IndexFieldNameNormalizer.normalize( NodeIndexPath.PERMISSIONS_READ.toString() ), "user:system:rmy" ).
            build();

        assertFalse( GetResultCanReadResolver.canRead( PrincipalKeys.empty(), resultFieldValues ) );
    }

    @Test
    public void anonymous_access()
        throws Exception
    {
        assertTrue( GetResultCanReadResolver.canRead( PrincipalKeys.empty(), ResultFieldValues.create().
            add( IndexFieldNameNormalizer.normalize( NodeIndexPath.PERMISSIONS_READ.toString() ), PrincipalKey.ofAnonymous().toString() ).
            build() ) );
    }

    @Test
    public void user_access()
        throws Exception
    {
        assertTrue(
            GetResultCanReadResolver.canRead( PrincipalKeys.from( PrincipalKey.from( "user:system:rmy" ) ), ResultFieldValues.create().
                add( IndexFieldNameNormalizer.normalize( NodeIndexPath.PERMISSIONS_READ.toString() ),
                     PrincipalKey.from( "user:system:rmy" ).toString() ).
                build() ) );
    }

    @Test
    public void group_access()
        throws Exception
    {
        assertTrue( GetResultCanReadResolver.canRead(
            PrincipalKeys.from( PrincipalKey.from( "user:system:rmy" ), PrincipalKey.from( "group:system:my-group" ) ),
            ResultFieldValues.create().
                add( IndexFieldNameNormalizer.normalize( NodeIndexPath.PERMISSIONS_READ.toString() ),
                     Arrays.asList( "user:system:rmy", "group:system:my-group" ) ).
                build() ) );
    }

}