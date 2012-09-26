package com.enonic.wem.core.jcr.old;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.jcr.NamespaceRegistry;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Workspace;
import javax.jcr.nodetype.NodeType;

import org.apache.jackrabbit.commons.cnd.CndImporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.wem.core.jcr.JcrConstants;

public class JcrInitializer
    implements JcrConstants
{

    private static final Logger LOG = LoggerFactory.getLogger( JcrInitializer.class );

    private JcrTemplate jcrTemplate;

    public void initializeJcrRepository()
    {
        jcrTemplate.execute( new JcrCallback()
        {
            @Override
            public Object doInJcr( JcrSession session )
                throws IOException, RepositoryException
            {
                initialize( session );
                return null;
            }
        } );
    }

    private void initialize( final JcrSession session )
        throws RepositoryException, IOException
    {
        registerNamespaces( session.getRealSession() );
        registerCustomNodeTypes( session.getRealSession() );
        createTreeStructure( session );
        session.save();
    }

    private void createTreeStructure( JcrSession session )
    {
        final JcrNode root = session.getRootNode();

        if ( root.hasNode( ROOT_NODE ) )
        {
            root.getNode( ROOT_NODE ).remove();
            session.save();
        }
        final JcrNode enonic = root.addNode( ROOT_NODE, JcrConstants.NT_UNSTRUCTURED );
        LOG.info( "Jcr node created: " + enonic.getPath() );
        final JcrNode userstores = enonic.addNode( USER_STORES_NODE, USER_STORES_TYPE );
        LOG.info( "Jcr node created: " + userstores.getPath() );
    }

    private void registerCustomNodeTypes( Session session )
        throws IOException
    {
        final InputStream in = getClass().getResourceAsStream( "/META-INF/jcr/node_types.cnd" );
        final Reader fileReader = new InputStreamReader( in );
        try
        {
            final NodeType[] nodeTypes = CndImporter.registerNodeTypes( fileReader, session, true );
            for ( NodeType nt : nodeTypes )
            {
                LOG.info( "Registered node type: " + nt.getName() );
            }
        }
        catch ( Exception e )
        {
            LOG.error( e.getMessage(), e );
        }
    }

    private void registerNamespaces( Session session )
        throws RepositoryException
    {
        final Workspace workspace = session.getWorkspace();
        final NamespaceRegistry reg = workspace.getNamespaceRegistry();

        final String[] prefixes = reg.getPrefixes();
        final Set<String> registeredPrefixes = new HashSet<String>( Arrays.<String>asList( prefixes ) );

        registerNamespace( reg, registeredPrefixes, WEM_NS_PREFIX, WEM_NS );
    }

    private void registerNamespace( NamespaceRegistry reg, Set<String> registeredPrefixes, String prefix, String uri )
        throws RepositoryException
    {
        if ( !registeredPrefixes.contains( prefix ) )
        {
            reg.registerNamespace( prefix, uri );
            LOG.info( "JCR namespace registered " + prefix + ":" + uri );
        }
        else
        {
            String registeredUri = reg.getURI( prefix );
            if ( !uri.equals( registeredUri ) )
            {
                throw new RepositoryRuntimeException(
                    "Namespace prefix is already registered with a different URI: " + prefix + ":" + registeredUri );
            }
        }
    }

    public void setJcrTemplate( JcrTemplate jcrTemplate )
    {
        this.jcrTemplate = jcrTemplate;
    }

}
