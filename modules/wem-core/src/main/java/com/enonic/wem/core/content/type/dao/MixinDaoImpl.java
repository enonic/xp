package com.enonic.wem.core.content.type.dao;


import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.content.QualifiedMixinNames;
import com.enonic.wem.api.content.type.Mixins;
import com.enonic.wem.api.content.type.form.Mixin;
import com.enonic.wem.api.content.type.form.QualifiedMixinName;
import com.enonic.wem.api.exception.SystemException;

@Component
public class MixinDaoImpl
    implements MixinDao
{

    @Override
    public void create( final Mixin mixin, final Session session )
    {
        try
        {
            new CreateMixinDaoHandler( session ).create( mixin );
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
            new UpdateMixinDaoHandler( session ).update( mixin );
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
            new DeleteMixinDaoHandler( session ).handle( qualifiedMixinName );
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
            return new RetrieveMixinDaoHandler( session ).retrieveAll();
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
            return new RetrieveMixinDaoHandler( session ).retrieve( qualifiedMixinNames );
        }
        catch ( RepositoryException e )
        {
            throw new SystemException( e, "Unable to retrieve Mixins [{0}]", qualifiedMixinNames );
        }
    }
}
