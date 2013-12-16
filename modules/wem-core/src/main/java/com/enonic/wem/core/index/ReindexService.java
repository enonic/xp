package com.enonic.wem.core.index;

import java.util.Collection;
import java.util.Iterator;

import javax.inject.Inject;
import javax.jcr.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.wem.api.account.Account;
import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.api.support.tree.Tree;
import com.enonic.wem.api.support.tree.TreeNode;
import com.enonic.wem.core.account.dao.AccountDao;
import com.enonic.wem.core.content.dao.ContentDao;
import com.enonic.wem.core.entity.dao.NodeJcrDao;
import com.enonic.wem.core.jcr.provider.JcrSessionProvider;


public class ReindexService
{
    private IndexService indexService;

    private JcrSessionProvider jcrSessionProvider;

    private AccountDao accountDao;

    private ContentDao contentDao;


    private final static Logger LOG = LoggerFactory.getLogger( ReindexService.class );

    public void reindexContent()
        throws Exception
    {
        Session session = jcrSessionProvider.login();

        reindexContentWEM( session );
        reindexContentNODB( session );
    }

    private void reindexContentWEM( final Session session )
    {
        final Tree<Content> contentTree = contentDao.getContentTree( session );

        final Iterator<TreeNode<Content>> rootElementsIterator = contentTree.iterator();
        while ( rootElementsIterator.hasNext() )
        {
            final TreeNode<Content> rootNode = rootElementsIterator.next();

            final Content content = rootNode.getObject();

            LOG.info( "Reindex root-content: " + content.getDisplayName() );

            indexService.indexContent( content );

            reindexChildren( rootNode );
        }
    }

    private void reindexContentNODB( final Session session )
    {
        final NodeJcrDao nodeJcrDao = new NodeJcrDao( session );

        final Nodes allNodes = nodeJcrDao.getNodesByParentPath( NodePath.ROOT );

        reindexNodes( allNodes, nodeJcrDao );
    }

    private void reindexNodes( final Nodes nodes, final NodeJcrDao nodeJcrDao )
    {
        final Iterator<Node> iterator = nodes.iterator();

        while ( iterator.hasNext() )
        {
            final Node node = iterator.next();

            LOG.info( "Reindex Node: " + node.name() );

            indexService.indexNode( node );

            reindexNodes( nodeJcrDao.getNodesByParentPath( node.path() ), nodeJcrDao );
        }
    }

    private void reindexChildren( final TreeNode<Content> childContent )
    {
        final Iterator<TreeNode<Content>> iterator = childContent.getChildren().iterator();

        while ( iterator.hasNext() )
        {
            final TreeNode<Content> childContentNode = iterator.next();

            final Content childeContent = childContentNode.getObject();
            LOG.info( "Reindex content: " + childeContent.getDisplayName() );
            indexService.indexContent( childeContent );

            reindexChildren( childContentNode );
        }
    }

    public void reindexAccounts()
        throws Exception
    {
        Session session = jcrSessionProvider.login();

        final Collection<AccountKey> accountKeys = accountDao.getAllAccountKeys( session );

        for ( AccountKey accountKey : accountKeys )
        {
            final Account account;

            if ( accountKey.isUser() )
            {
                account = accountDao.findUser( accountKey.asUser(), true, false, session );
            }
            else if ( accountKey.isGroup() )
            {
                account = accountDao.findGroup( accountKey.asGroup(), true, session );
            }
            else
            {
                return;
            }

            LOG.info( "Reindex account: " + account.getDisplayName() );

            indexService.indexAccount( account );
        }
    }

    @Inject
    public void setAccountDao( final AccountDao accountDao )
    {
        this.accountDao = accountDao;
    }

    @Inject
    public void setContentDao( final ContentDao contentDao )
    {
        this.contentDao = contentDao;
    }

    @Inject
    public void setJcrSessionProvider( final JcrSessionProvider jcrSessionProvider )
    {
        this.jcrSessionProvider = jcrSessionProvider;
    }

    @Inject
    public void setIndexService( final IndexService indexService )
    {
        this.indexService = indexService;
    }


}
