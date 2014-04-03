package com.enonic.wem.core.schema;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

import com.enonic.wem.api.command.schema.content.ContentTypeService;
import com.enonic.wem.api.command.schema.mixin.MixinService;
import com.enonic.wem.api.command.schema.relationship.RelationshipTypeService;
import com.enonic.wem.core.command.CommandBinder;
import com.enonic.wem.core.initializer.InitializerTaskBinder;
import com.enonic.wem.core.schema.content.ContentTypeServiceImpl;
import com.enonic.wem.core.schema.content.ContentTypesInitializer;
import com.enonic.wem.core.schema.content.DemoImagesInitializer;
import com.enonic.wem.core.schema.content.dao.ContentTypeDao;
import com.enonic.wem.core.schema.content.dao.ContentTypeDaoImpl;
import com.enonic.wem.core.schema.mixin.MixinServiceImpl;
import com.enonic.wem.core.schema.mixin.MixinsInitializer;
import com.enonic.wem.core.schema.mixin.dao.MixinDao;
import com.enonic.wem.core.schema.mixin.dao.MixinDaoImpl;
import com.enonic.wem.core.schema.relationship.RelationshipTypeServiceImpl;
import com.enonic.wem.core.schema.relationship.RelationshipTypesInitializer;
import com.enonic.wem.core.schema.relationship.dao.RelationshipTypeDao;
import com.enonic.wem.core.schema.relationship.dao.RelationshipTypeDaoImpl;

public final class SchemaModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind( RelationshipTypeDao.class ).to( RelationshipTypeDaoImpl.class ).in( Scopes.SINGLETON );
        bind( ContentTypeDao.class ).to( ContentTypeDaoImpl.class ).in( Scopes.SINGLETON );
        bind( MixinDao.class ).to( MixinDaoImpl.class ).in( Scopes.SINGLETON );
        bind( MixinService.class ).to( MixinServiceImpl.class ).in( Scopes.SINGLETON );
        bind( ContentTypeService.class ).to( ContentTypeServiceImpl.class ).in( Scopes.SINGLETON );
        bind( RelationshipTypeService.class ).to( RelationshipTypeServiceImpl.class ).in( Scopes.SINGLETON );

        final InitializerTaskBinder tasks = InitializerTaskBinder.from( binder() );
        tasks.add( ContentTypesInitializer.class );
        tasks.add( MixinsInitializer.class );
        tasks.add( RelationshipTypesInitializer.class );
        tasks.add( DemoImagesInitializer.class );

        final CommandBinder commands = CommandBinder.from( binder() );

        commands.add( GetSchemasHandler.class );
        commands.add( GetRootSchemasHandler.class );
        commands.add( GetChildSchemasHandler.class );
    }
}
