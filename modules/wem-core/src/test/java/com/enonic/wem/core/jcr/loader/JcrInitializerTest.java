package com.enonic.wem.core.jcr.loader;

import javax.jcr.Repository;
import javax.jcr.Session;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.core.jcr.provider.JcrSessionProviderImpl;
import com.enonic.wem.core.jcr.repository.JcrMicroKernelFactory;
import com.enonic.wem.core.jcr.repository.JcrRepositoryFactory;

import static org.junit.Assert.*;

public class JcrInitializerTest
{
    private JcrMicroKernelFactory jcrMicroKernelFactory;

    private Repository repo;

    @Before
    public final void before()
        throws Exception
    {
        jcrMicroKernelFactory = new JcrMicroKernelFactory();
        jcrMicroKernelFactory.setInMemoryRepository( true );
        jcrMicroKernelFactory.init();

        final JcrRepositoryFactory jcrRepositoryFactory = new JcrRepositoryFactory();
        jcrRepositoryFactory.setMicroKernel( jcrMicroKernelFactory.getObject() );
        jcrRepositoryFactory.init();
        repo = jcrRepositoryFactory.getObject();
    }

    @After
    public final void after()
    {
        jcrMicroKernelFactory.dispose();
    }

    @Test
    public void testInitialize()
        throws Exception
    {
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
