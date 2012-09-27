package com.enonic.wem.core.jcr.loader;

import javax.jcr.Session;

import org.apache.jackrabbit.oak.jcr.RepositoryImpl;
import org.junit.Test;

import static org.junit.Assert.*;

import com.enonic.wem.core.jcr.provider.JcrSessionProviderImpl;

public class JcrInitializerTest
{
    @Test
    public void testInitialize()
        throws Exception
    {
        final RepositoryImpl repo = new RepositoryImpl();

        final JcrSessionProviderImpl sessionProvider = new JcrSessionProviderImpl();
        sessionProvider.setRepository( repo );

        final JcrInitializer initializer = new JcrInitializer( sessionProvider );
        initializer.initialize();

        verifyInitialize( sessionProvider.loginAdmin() );
    }

    private void verifyInitialize( final Session session )
        throws Exception
    {
        assertNodeAndType( session, "wem", "nt:unstructured" );
        assertNodeAndType( session, "wem/userStores", "wem:userStores" );
        assertNodeAndType( session, "wem/userStores/system", "wem:userStore" );
        assertNodeAndType( session, "wem/userStores/system/users", "wem:users" );
        assertNodeAndType( session, "wem/userStores/system/groups", "wem:groups" );
        assertNodeAndType( session, "wem/userStores/system/roles", "wem:roles" );
    }

    private void assertNodeAndType( final Session session, final String relPath, final String nodeType )
        throws Exception
    {
        assertTrue( session.getRootNode().hasNode( relPath ) );
        assertEquals( nodeType, session.getRootNode().getNode( relPath ).getPrimaryNodeType().getName() );
    }
}
