package com.enonic.wem.export.internal.builder;

import com.google.common.base.Strings;

import com.enonic.wem.api.index.ChildOrder;
import com.enonic.wem.api.node.CreateNodeParams;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.export.internal.xml.XmlNode;

public class NodeXmlBuilder
{
    public static CreateNodeParams build( final XmlNode xmlNode, final NodePath importRoot )
    {
        return CreateNodeParams.create().
            name( xmlNode.getName() ).
            parent( ImportNodeParentPathResolver.resolve( xmlNode.getParent(), importRoot ) ).
            childOrder( getChildOrder( xmlNode ) ).
            data( PropertyTreeXmlBuilder.build( xmlNode.getProperties() ) ).
            build();
    }

    private static ChildOrder getChildOrder( final XmlNode xmlNode )
    {
        if ( Strings.isNullOrEmpty( xmlNode.getChildOrder() ) )
        {
            return null;
        }

        return ChildOrder.from( xmlNode.getChildOrder() );
    }

}
