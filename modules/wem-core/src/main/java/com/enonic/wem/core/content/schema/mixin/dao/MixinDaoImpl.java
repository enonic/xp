package com.enonic.wem.core.content.schema.mixin.dao;


import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.content.schema.mixin.Mixin;
import com.enonic.wem.api.content.schema.mixin.Mixins;
import com.enonic.wem.api.content.schema.mixin.QualifiedMixinName;
import com.enonic.wem.api.content.schema.mixin.QualifiedMixinNames;
import com.enonic.wem.api.exception.SystemException;


public class MixinDaoImpl
    implements MixinDao
{

    @Override
    public void create( final Mixin mixin, final Session session )
    {
        try
        {
            new MixinDaoHandlerCreate( session ).create( mixin );
        }
        catch ( RepositoryException e )
        {
            throw new SystemException( e, "Unable to create Mixin [{0}]", mixin );
        }
    }

    @Override
    public void update( final Mixin mixin, final Session session )
    {
        try
        {
            new MixinDaoHandlerUpdate( session ).update( mixin );
        }
        catch ( RepositoryException e )
        {
            throw new SystemException( e, "Unable to update Mixin [{0}]", mixin );
        }
    }

    @Override
    public void delete( final QualifiedMixinName qualifiedMixinName, final Session session )
    {
        try
        {
            new MixinDaoHandlerDelete( session ).handle( qualifiedMixinName );
        }
        catch ( RepositoryException e )
        {
            throw new SystemException( e, "Unable to delete Mixin [{0}]", qualifiedMixinName );
        }
    }

    @Override
    public Mixins selectAll( final Session session )
    {
        try
        {
            return new MixinDaoHandlerSelect( session ).retrieveAll();
        }
        catch ( RepositoryException e )
        {
            throw new SystemException( e, "Unable to retrieve all Mixins" );
        }
    }

    @Override
    public Mixins select( final QualifiedMixinNames qualifiedMixinNames, Session session )
    {
        try
        {
            return new MixinDaoHandlerSelect( session ).retrieve( qualifiedMixinNames );
        }
        catch ( RepositoryException e )
        {
            throw new SystemException( e, "Unable to retrieve Mixins [{0}]", qualifiedMixinNames );
        }
    }
}
