package com.enonic.wem.core.index;

import java.util.Collection;
import java.util.Iterator;

import javax.inject.Inject;
import javax.jcr.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.account.Account;
import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.support.tree.Tree;
import com.enonic.wem.api.support.tree.TreeNode;
import com.enonic.wem.core.account.dao.AccountDao;
import com.enonic.wem.core.content.dao.ContentDao;
import com.enonic.wem.core.jcr.provider.JcrSessionProvider;

@Component
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

    public void reindexChildren( final TreeNode<Content> node )
    {
        final Iterator<TreeNode<Content>> iterator = node.getChildren().iterator();

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
