package com.enonic.wem.core.jcr;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.jcr.ImportUUIDBehavior;
import javax.jcr.NamespaceRegistry;
import javax.jcr.Session;
import javax.jcr.Workspace;

import org.apache.jackrabbit.commons.cnd.CndImporter;

import com.enonic.wem.core.jcr.provider.JcrSessionProvider;

public final class JcrInitializer
    implements JcrConstants
{
    private final JcrSessionProvider jcrSessionProvider;

    public JcrInitializer( final JcrSessionProvider jcrSessionProvider )
    {
        this.jcrSessionProvider = jcrSessionProvider;
    }

    public void initialize()
        throws Exception
    {
        final Session session = this.jcrSessionProvider.loginAdmin();

        try
        {
            initialize( session );
        }
        finally
        {
            session.logout();
        }
    }

    private void initialize( final Session session )
        throws Exception
    {
        registerNamespaces( session );
        registerNodeTypes( session, "/META-INF/jcr/node_types.cnd" );
        importContent( session, "/META-INF/jcr/init_content.xml" );
    }

    private void registerNamespaces( final Session session )
        throws Exception
    {
        final Workspace workspace = session.getWorkspace();
        final NamespaceRegistry reg = workspace.getNamespaceRegistry();

        try
        {
            reg.registerNamespace( WEM_NS_PREFIX, WEM_NS );
        }
        catch ( final Exception e )
        {
            // Do nothing
        }
    }

    private void registerNodeTypes( final Session session, final String resource )
        throws Exception
    {
        final InputStream in = getClass().getResourceAsStream( resource );
        final Reader fileReader = new InputStreamReader( in );
        CndImporter.registerNodeTypes( fileReader, session, true );
    }

    private void importContent( final Session session, final String resource )
        throws Exception
    {
        final InputStream in = getClass().getResourceAsStream( resource );
        session.getWorkspace().importXML( "/", in, ImportUUIDBehavior.IMPORT_UUID_COLLISION_REPLACE_EXISTING );
    }
}
