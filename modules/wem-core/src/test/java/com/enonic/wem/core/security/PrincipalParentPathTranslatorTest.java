package com.enonic.wem.core.security;

import org.junit.Test;

import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.UserStoreKey;
import com.enonic.wem.core.entity.NodePath;

import static org.junit.Assert.*;

public class PrincipalParentPathTranslatorTest
{
    @Test
    public void user()
        throws Exception
    {
        final NodePath nodePath = PrincipalParentPathTranslator.toParentPath( PrincipalKey.ofUser( UserStoreKey.system(), "rmy" ) );
        assertEquals( "/system/USER", nodePath.toString() );
    }
}