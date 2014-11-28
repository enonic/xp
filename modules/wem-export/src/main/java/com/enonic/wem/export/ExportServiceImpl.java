package com.enonic.wem.export;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.wem.api.export.ExportService;
import com.enonic.wem.api.node.FindNodesByParentParams;
import com.enonic.wem.api.node.FindNodesByParentResult;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.node.NodeService;
import com.enonic.wem.export.internal.xml.XmlNode;
import com.enonic.wem.export.internal.xml.mapper.XmlNodeMapper;
import com.enonic.wem.export.internal.xml.serializer.XmlNodeSerializer;

@Component(immediate = true)
public class ExportServiceImpl
    implements ExportService
{
    private NodeService nodeService;

    private XmlNodeSerializer xmlNodeSerializer = new XmlNodeSerializer();

    private final static int BATCH_SIZE = 10;

    @Override
    public void export( final NodePath nodePath )
    {
        doProcessChildren( nodePath );
    }

    private void doProcessChildren( final NodePath nodePath )
    {
        final FindNodesByParentResult countResult = nodeService.findByParent( FindNodesByParentParams.create().
            countOnly( true ).
            parentPath( nodePath ).
            build() );

        final long totalHits = countResult.getTotalHits();

        final long batches = totalHits % BATCH_SIZE;

        for ( int i = 0; i <= batches; i++ )
        {
            final FindNodesByParentResult currentLevelChildren = nodeService.findByParent( FindNodesByParentParams.create().
                parentPath( nodePath ).
                from( i * BATCH_SIZE ).
                size( BATCH_SIZE ).
                build() );

            for ( final Node child : currentLevelChildren.getNodes() )
            {
                exportChild( child );
                doProcessChildren( child.path() );
            }
        }
    }

    private void exportChild( final Node child )
    {
        final XmlNode xmlNode = XmlNodeMapper.toXml( child );

        final String serializedNode = this.xmlNodeSerializer.serialize( xmlNode );

        System.out.println( serializedNode );
    }

    @Reference
    public void setNodeService( final NodeService nodeService )
    {
        this.nodeService = nodeService;
    }
}
