package com.enonic.wem.core.index;

import java.util.Iterator;

import javax.inject.Inject;
import javax.jcr.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.core.entity.dao.NodeJcrDao;
import com.enonic.wem.core.jcr.provider.JcrSessionProvider;


public class ReindexService
{
    private IndexService indexService;

    private JcrSessionProvider jcrSessionProvider;

    private final static Logger LOG = LoggerFactory.getLogger( ReindexService.class );

    public void reindexContent()
        throws Exception
    {
        Session session = jcrSessionProvider.login();

        reindexContentNODB( session );
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

            if ( !node.name().toString().startsWith( "__" ) )
            {
                indexService.indexNode( node );
            }

            reindexNodes( nodeJcrDao.getNodesByParentPath( node.path() ), nodeJcrDao );
        }
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
