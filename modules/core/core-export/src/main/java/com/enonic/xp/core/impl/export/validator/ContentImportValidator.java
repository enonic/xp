package com.enonic.xp.core.impl.export.validator;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.CreateNodeParams;

public class ContentImportValidator
    implements ImportValidator
{
    private static final ChildOrder OLD_DEFAULT_CHILD_ORDER = ChildOrder.from( "_modifiedtime DESC" );

    @Override
    public CreateNodeParams ensureValid( final CreateNodeParams original )
    {
        final CreateNodeParams.Builder builder = CreateNodeParams.create( original );

        final PropertyTree updatedData = original.getData();

        validateChildOrder( original, builder );

        builder.data( updatedData );

        return builder.build();
    }

    private void validateChildOrder( final CreateNodeParams original, final CreateNodeParams.Builder builder )
    {
        final ChildOrder childOrder = original.getChildOrder();

        if ( childOrder.getOrderExpressions() == null || childOrder.getOrderExpressions().isEmpty() )
        {
            builder.childOrder( ContentConstants.DEFAULT_CHILD_ORDER );
        }
        else
        {

            if ( childOrder.equals( ChildOrder.defaultOrder() ) || childOrder.equals( OLD_DEFAULT_CHILD_ORDER ) )
            {
                builder.childOrder( ContentConstants.DEFAULT_CHILD_ORDER );
            }
        }
    }

    @Override
    public boolean canHandle( final CreateNodeParams original )
    {
        return original.getNodeType().equals( ContentConstants.CONTENT_NODE_COLLECTION );
    }
}
