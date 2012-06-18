package com.enonic.wem.core.jcr;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.jcr.NamespaceRegistry;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Workspace;
import javax.jcr.nodetype.NodeType;

import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.commons.cnd.CndImporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import static com.enonic.wem.core.jcr.JcrCmsConstants.ENONIC_CMS_NAMESPACE;
import static com.enonic.wem.core.jcr.JcrCmsConstants.ENONIC_CMS_NAMESPACE_PREFIX;
import static com.enonic.wem.core.jcr.JcrCmsConstants.GROUPS_NODE;
import static com.enonic.wem.core.jcr.JcrCmsConstants.GROUPS_NODE_TYPE;
import static com.enonic.wem.core.jcr.JcrCmsConstants.ROLES_NODE;
import static com.enonic.wem.core.jcr.JcrCmsConstants.ROLES_NODE_TYPE;
import static com.enonic.wem.core.jcr.JcrCmsConstants.ROOT_NODE;
import static com.enonic.wem.core.jcr.JcrCmsConstants.SYSTEM_USERSTORE_KEY;
import static com.enonic.wem.core.jcr.JcrCmsConstants.SYSTEM_USERSTORE_NODE;
import static com.enonic.wem.core.jcr.JcrCmsConstants.USERSTORES_NODE;
import static com.enonic.wem.core.jcr.JcrCmsConstants.USERSTORES_NODE_TYPE;
import static com.enonic.wem.core.jcr.JcrCmsConstants.USERSTORE_NODE_TYPE;
import static com.enonic.wem.core.jcr.JcrCmsConstants.USERS_NODE;
import static com.enonic.wem.core.jcr.JcrCmsConstants.USERS_NODE_TYPE;

//@Component
public class JcrBootstrap
{
    private static final Logger LOG = LoggerFactory.getLogger( JcrBootstrap.class );

    private Resource compactNodeDefinitionFile;

    @Autowired
    private JcrTemplate jcrTemplate;

    @Autowired
    private JcrAccountsImporter jcrAccountsImporter;

    public JcrBootstrap()
    {
    }

    @PostConstruct
    public void afterPropertiesSet()
        throws Exception
    {
        jcrTemplate.execute( new JcrCallback()
        {
            @Override
            public Object doInJcr( JcrSession session )
                    throws IOException, RepositoryException
            {
                initialize( session.getRealSession() );
                return null;
            }
        } );
    }

    public void initialize( final Session jcrSession )
            throws RepositoryException, IOException
    {
        LOG.info( "Initializing JCR repository..." );
        try
        {
            registerNamespaces( jcrSession );

            registerCustomNodeTypes( jcrSession );

            createTreeStructure( jcrSession );

            jcrSession.save();

            jcrAccountsImporter.importAccounts();

            // log imported tree
//            LOG.info( JcrHelper.sessionViewToXml( jcrSession, "/enonic" ) );

            jcrSession.save();
        }
        catch ( Exception e )
        {
            throw new RepositoryRuntimeException( "Error while initializing JCR repository", e );
        }

        LOG.info( "JCR repository initialized" );
    }

    private void createTreeStructure( Session jcrSession )
        throws RepositoryException
    {
        Node root = jcrSession.getRootNode();

        if ( root.hasNode( ROOT_NODE ) )
        {
            root.getNode( ROOT_NODE ).remove();
            jcrSession.save();
        }
        Node enonic = root.addNode( ROOT_NODE, JcrConstants.NT_UNSTRUCTURED );
        Node userstores = enonic.addNode( USERSTORES_NODE, USERSTORES_NODE_TYPE );

        Node systemUserstore = userstores.addNode( SYSTEM_USERSTORE_NODE, USERSTORE_NODE_TYPE );
        systemUserstore.setProperty( "key", Integer.toString( SYSTEM_USERSTORE_KEY ) );
        systemUserstore.setProperty( "default", true );
        systemUserstore.setProperty( "connector", "" );
        systemUserstore.setProperty( "xmlconfig", "" );

        Node groupsRoles = systemUserstore.addNode( GROUPS_NODE, GROUPS_NODE_TYPE );
        Node usersRoles = systemUserstore.addNode( USERS_NODE, USERS_NODE_TYPE );
        Node systemRoles = systemUserstore.addNode( ROLES_NODE, ROLES_NODE_TYPE );

        systemRoles.addNode( "ea", "cms:role" );
        systemRoles.addNode( "developer", "cms:role" );
        systemRoles.addNode( "administrator", "cms:role" );
        systemRoles.addNode( "contributor", "cms:role" );
        systemRoles.addNode( "expert", "cms:role" );
        systemRoles.addNode( "everyone", "cms:role" );
        systemRoles.addNode( "authenticated", "cms:role" );
    }

    private void registerCustomNodeTypes( Session jcrSession )
        throws RepositoryException, IOException
    {
        Reader fileReader = new InputStreamReader( compactNodeDefinitionFile.getInputStream() );
        try
        {
            NodeType[] nodeTypes = CndImporter.registerNodeTypes( fileReader, jcrSession, true );
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

    private void registerNamespaces( Session jcrSession )
        throws RepositoryException
    {
        Workspace workspace = jcrSession.getWorkspace();
        NamespaceRegistry reg = workspace.getNamespaceRegistry();

        String[] prefixes = reg.getPrefixes();
        Set<String> registeredPrefixes = new HashSet<String>( Arrays.<String>asList( prefixes ) );

        registerNamespace( reg, registeredPrefixes, ENONIC_CMS_NAMESPACE_PREFIX, ENONIC_CMS_NAMESPACE );
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

    @Value("classpath:com/enonic/wem/core/jcr/cmstypes.cnd")
    public void setCompactNodeDefinitionFile( Resource compactNodeDefinitionFile )
    {
        this.compactNodeDefinitionFile = compactNodeDefinitionFile;
    }
}
