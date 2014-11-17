package com.enonic.wem.core.security;

import org.junit.Test;

import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.UserStoreKey;
import com.enonic.wem.api.node.NodePath;

import static org.junit.Assert.*;

public class PrincipalPathTranslatorTest
{
    @Test
    public void userParentPath()
        throws Exception
    {
        final NodePath nodePath = PrincipalPathTranslator.toParentPath( PrincipalKey.ofUser( UserStoreKey.system(), "rmy" ) );
        assertEquals( "/system/user", nodePath.toString() );
    }

    @Test
    public void userPath()
        throws Exception
    {
        final NodePath nodePath = PrincipalPathTranslator.toPath( PrincipalKey.ofUser( UserStoreKey.system(), "rmy" ) );
        assertEquals( "/system/user/rmy", nodePath.toString() );
    }
}