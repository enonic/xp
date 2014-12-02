package com.enonic.wem.export.internal.builder;

import com.enonic.wem.api.index.ChildOrder;
import com.enonic.wem.api.node.CreateNodeParams;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.export.internal.xml.XmlNode;

public class NodeXmlBuilder
{
    public static CreateNodeParams build( final XmlNode xmlNode )
    {
        return CreateNodeParams.create().
            name( xmlNode.getName() ).
            parent( NodePath.newPath( xmlNode.getParent() ).build() ).
            childOrder( ChildOrder.from( xmlNode.getChildOrder() ) ).
            // propertyTree
                data( PropertyTreeXmlBuilder.build( xmlNode.getProperties() ) ).
            // ACL
                // indexConfigDocument
                build();
    }

}
