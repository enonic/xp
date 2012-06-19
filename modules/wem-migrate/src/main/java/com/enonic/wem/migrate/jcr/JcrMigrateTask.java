package com.enonic.wem.migrate.jcr;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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
import org.springframework.stereotype.Component;

import com.enonic.wem.core.jcr.JcrCallback;
import com.enonic.wem.core.jcr.JcrSession;
import com.enonic.wem.core.jcr.JcrTemplate;
import com.enonic.wem.core.jcr.RepositoryRuntimeException;
import com.enonic.wem.migrate.MigrateTask;

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

@Component
public class JcrMigrateTask implements MigrateTask
{
    private static final Logger LOG = LoggerFactory.getLogger( JcrMigrateTask.class );

    private Resource compactNodeDefinitionFile;

    private JcrTemplate jcrTemplate;

    private JcrAccountsImporter jcrAccountsImporter;

    public JcrMigrateTask()
    {
    }

    @Override
    public void migrate()
            throws Exception
    {
        jcrTemplate.execute( new JcrCallback()
        {
            @Override
            public Object doInJcr( JcrSession session )
                    throws IOException, RepositoryException
            {
                migrateToJcr( session.getRealSession() );
                return null;
            }
        } );
    }

    private void migrateToJcr( final Session session )
            throws RepositoryException, IOException
    {
        registerNamespaces( session );
        registerCustomNodeTypes( session );
        createTreeStructure( session );
        session.save();
        jcrAccountsImporter.importAccounts();
        session.save();
        // log imported tree
//            LOG.info( JcrHelper.sessionViewToXml( session, "/enonic" ) );
    }

    private void createTreeStructure( Session session )
        throws RepositoryException
    {
        Node root = session.getRootNode();

        if ( root.hasNode( ROOT_NODE ) )
        {
            root.getNode( ROOT_NODE ).remove();
            session.save();
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

    private void registerCustomNodeTypes( Session session )
        throws RepositoryException, IOException
    {
        Reader fileReader = new InputStreamReader( compactNodeDefinitionFile.getInputStream() );
        try
        {
            NodeType[] nodeTypes = CndImporter.registerNodeTypes( fileReader, session, true );
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
        Workspace workspace = session.getWorkspace();
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

    @Autowired
    public void setJcrTemplate( JcrTemplate jcrTemplate )
    {
        this.jcrTemplate = jcrTemplate;
    }

    @Autowired
    public void setJcrAccountsImporter( JcrAccountsImporter jcrAccountsImporter )
    {
        this.jcrAccountsImporter = jcrAccountsImporter;
    }
}
