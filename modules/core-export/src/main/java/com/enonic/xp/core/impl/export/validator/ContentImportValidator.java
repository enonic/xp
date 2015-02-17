package com.enonic.xp.core.impl.export.validator;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;

public class ContentImportValidator
    implements ImportValidator
{
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
            final ChildOrder oldDefaultChildOrder = ChildOrder.from( "_modifiedtime DESC" );

            if ( childOrder.equals( ChildOrder.defaultOrder() ) || childOrder.equals( oldDefaultChildOrder ) )
            {
                builder.childOrder( ContentConstants.DEFAULT_CHILD_ORDER );
            }
        }
    }

    private User getUser()
    {

        final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();

        if ( authInfo == null || authInfo.getUser() == null )
        {
            return User.ANONYMOUS;
        }

        return authInfo.getUser();
    }

    @Override
    public boolean canHandle( final CreateNodeParams original )
    {
        return original.getNodeType().equals( ContentConstants.CONTENT_NODE_COLLECTION );
    }
}
