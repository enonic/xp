package com.enonic.wem.core.schema;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

import com.enonic.wem.api.schema.SchemaRegistry;
import com.enonic.wem.api.schema.SchemaService;
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.api.schema.mixin.MixinService;
import com.enonic.wem.api.schema.relationship.RelationshipTypeService;
import com.enonic.wem.core.initializer.InitializerTaskBinder;
import com.enonic.wem.core.schema.content.ContentTypeServiceImpl;
import com.enonic.wem.core.schema.content.DemoImagesInitializer;
import com.enonic.wem.core.schema.content.dao.ContentTypeDao;
import com.enonic.wem.core.schema.content.dao.ContentTypeDaoImpl;
import com.enonic.wem.core.schema.mixin.MixinServiceImpl;
import com.enonic.wem.core.schema.mixin.dao.MixinDao;
import com.enonic.wem.core.schema.mixin.dao.MixinDaoImpl;
import com.enonic.wem.core.schema.relationship.RelationshipTypeServiceImpl;
import com.enonic.wem.core.schema.relationship.dao.RelationshipTypeDao;
import com.enonic.wem.core.schema.relationship.dao.RelationshipTypeDaoImpl;

public final class SchemaModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind( SchemaRegistry.class ).to( SchemaRegistryImpl.class ).in( Scopes.SINGLETON );
        bind( RelationshipTypeDao.class ).to( RelationshipTypeDaoImpl.class ).in( Scopes.SINGLETON );
        bind( ContentTypeDao.class ).to( ContentTypeDaoImpl.class ).in( Scopes.SINGLETON );
        bind( MixinDao.class ).to( MixinDaoImpl.class ).in( Scopes.SINGLETON );
        bind( MixinService.class ).to( MixinServiceImpl.class ).in( Scopes.SINGLETON );
        bind( ContentTypeService.class ).to( ContentTypeServiceImpl.class ).in( Scopes.SINGLETON );
        bind( RelationshipTypeService.class ).to( RelationshipTypeServiceImpl.class ).in( Scopes.SINGLETON );
        bind( SchemaService.class ).to( SchemaServiceImpl.class ).in( Scopes.SINGLETON );

        final InitializerTaskBinder tasks = InitializerTaskBinder.from( binder() );
        tasks.add( DemoImagesInitializer.class );
    }
}
