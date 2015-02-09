package com.enonic.wem.export.internal.validator;

import java.time.Instant;

import com.enonic.wem.api.content.ContentConstants;
import com.enonic.wem.api.content.ContentPropertyNames;
import com.enonic.wem.api.context.ContextAccessor;
import com.enonic.wem.api.data.PropertyTree;
import com.enonic.wem.api.index.ChildOrder;
import com.enonic.wem.api.node.CreateNodeParams;
import com.enonic.wem.api.security.User;
import com.enonic.wem.api.security.auth.AuthenticationInfo;

public class ContentImportValidator
    implements ImportValidator
{
    public CreateNodeParams ensureValid( final CreateNodeParams original )
    {

        final CreateNodeParams.Builder builder = CreateNodeParams.create( original );

        final Instant now = Instant.now();

        final PropertyTree updatedData = original.getData();

        if ( updatedData.getProperty( ContentPropertyNames.CREATED_TIME ) == null )
        {
            updatedData.setInstant( ContentPropertyNames.CREATED_TIME, now );
        }

        if ( updatedData.getProperty( ContentPropertyNames.CREATOR ) == null )
        {
            updatedData.setString( ContentPropertyNames.CREATOR, getUser().getKey().toString() );
        }

        if ( updatedData.getProperty( ContentPropertyNames.MODIFIED_TIME ) == null )
        {
            updatedData.setInstant( ContentPropertyNames.MODIFIED_TIME, now );
        }

        if ( updatedData.getProperty( ContentPropertyNames.MODIFIER ) == null )
        {
            updatedData.setString( ContentPropertyNames.MODIFIER, getUser().getKey().toString() );
        }

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
