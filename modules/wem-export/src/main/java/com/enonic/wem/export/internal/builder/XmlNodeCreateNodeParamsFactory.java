package com.enonic.wem.export.internal.builder;

import com.google.common.base.Strings;

import com.enonic.wem.api.index.ChildOrder;
import com.enonic.wem.api.node.CreateNodeParams;
import com.enonic.wem.api.node.InsertManualStrategy;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.export.internal.xml.XmlNode;

public class XmlNodeCreateNodeParamsFactory
{
    public static CreateNodeParams build( final XmlNode xmlNode, final NodePath nodeImportPath )
    {
        final String nodeName = nodeImportPath.getLastElement().toString();
        final NodePath parentPath = nodeImportPath.getParentPath();

        final ChildOrder childOrder = getChildOrder( xmlNode );

        final CreateNodeParams.Builder builder = CreateNodeParams.create().
            name( nodeName ).
            parent( parentPath ).
            childOrder( childOrder ).
            insertManualStrategy( InsertManualStrategy.LAST ).
            data( PropertyTreeXmlBuilder.build( xmlNode.getProperties() ) );

        return builder.build();
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
