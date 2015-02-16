package com.enonic.xp.core.impl.export.validator;

import java.time.Instant;

import org.apache.commons.lang.StringUtils;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;

public class ContentImportValidator
    implements ImportValidator
{
    private final static String DEFAULT_MODULE = "com-enonic-web-enonic-com";

    public CreateNodeParams ensureValid( final CreateNodeParams original )
    {

        final CreateNodeParams.Builder builder = CreateNodeParams.create( original );

        final Instant now = Instant.now();

        final PropertyTree updatedData = original.getData().copy();

        if ( original.getData().hasProperty( "x" ) )
        {
            final PropertySet xData = original.getData().getSet( "x" );
            final PropertySet newXData = updatedData.getSet( "x" );
            for ( String name : xData.getPropertyNames() )
            {
                final String moduleName;
                final String mixinName;
                if ( name.contains( ":" ) )
                {
                    moduleName = StringUtils.substringBefore( name, ":" ).replace( '.', '-' );
                    mixinName = StringUtils.substringAfter( name, ":" );
                }
                else
                {
                    moduleName = DEFAULT_MODULE;
                    mixinName = name;
                }

                final PropertySet xDataModule;
                if ( !newXData.hasProperty( moduleName ) )
                {
                    xDataModule = newXData.addSet( moduleName );
                }
                else
                {
                    xDataModule = newXData.getSet( moduleName );
                }

                final PropertySet xDataModuleName;
                if ( !xDataModule.hasProperty( mixinName ) )
                {
                    xDataModuleName = xDataModule.addSet( mixinName );
                }
                else
                {
                    xDataModuleName = xDataModule.getSet( mixinName );
                }

                removePropertyChildren( newXData.getSet( name ) );
                newXData.removeProperties( name );
                newXData.removeProperty( name );

                final PropertySet oldXData = xData.getSet( name );
                for ( String oldName : oldXData.getPropertyNames() )
                {
                    oldXData.getProperty( oldName ).copyTo( xDataModuleName );
                }
            }
        }

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

    private void removePropertyChildren( final PropertySet propertySet )
    {
        for ( String name : propertySet.getPropertyNames() )
        {
            if ( propertySet.getProperty( name ).getValue().isSet() )
            {
                removePropertyChildren( propertySet.getSet( name ) );
            }
            propertySet.removeProperty( name );
        }
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
