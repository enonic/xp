package com.enonic.wem.core.jcr;

import java.io.ByteArrayOutputStream;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.jdom.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.cms.framework.util.JDOMUtil;

public class JcrHelper
{
    private static final Logger LOG = LoggerFactory.getLogger( JcrHelper.class );

    public static void printNode( Node node )
        throws RepositoryException
    {
        String nodeStr = node.getPath() + " [" + node.getPrimaryNodeType().getName() + "]";
        LOG.info( nodeStr );
        NodeIterator nodeIterator = node.getNodes();
        while ( nodeIterator.hasNext() )
        {
            Node childNode = nodeIterator.nextNode();
            printNode( childNode );
        }
    }

    public static String sessionViewToXml( Session jcrSession, String absolutePath )
    {
        return sessionViewToXml( jcrSession, absolutePath, true );
    }

    public static String sessionViewToXml( Session jcrSession, String absolutePath, boolean prettyPrint )
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try
        {
            jcrSession.exportSystemView( absolutePath, out, true, false );
            String result = out.toString();
            if ( prettyPrint )
            {
                Document xmlDoc = JDOMUtil.parseDocument( result );
                result = JDOMUtil.prettyPrintDocument( xmlDoc );
            }
            return result;
        }
        catch ( Exception e )
        {
            LOG.warn( "Unable to serialize JCR session view", e );
        }
        return null;
    }
}
