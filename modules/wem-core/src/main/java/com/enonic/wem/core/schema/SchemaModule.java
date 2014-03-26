package com.enonic.wem.core.schema;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

import com.enonic.wem.api.command.schema.mixin.MixinService;
import com.enonic.wem.core.command.CommandBinder;
import com.enonic.wem.core.initializer.InitializerTaskBinder;
import com.enonic.wem.core.schema.content.ContentTypesInitializer;
import com.enonic.wem.core.schema.content.CreateContentTypeHandler;
import com.enonic.wem.core.schema.content.DeleteContentTypeHandler;
import com.enonic.wem.core.schema.content.DemoImagesInitializer;
import com.enonic.wem.core.schema.content.GetAllContentTypesHandler;
import com.enonic.wem.core.schema.content.GetChildContentTypesHandler;
import com.enonic.wem.core.schema.content.GetContentTypeHandler;
import com.enonic.wem.core.schema.content.GetContentTypesHandler;
import com.enonic.wem.core.schema.content.GetRootContentTypesHandler;
import com.enonic.wem.core.schema.content.UpdateContentTypeHandler;
import com.enonic.wem.core.schema.content.ValidateContentTypeHandler;
import com.enonic.wem.core.schema.content.dao.ContentTypeDao;
import com.enonic.wem.core.schema.content.dao.ContentTypeDaoImpl;
import com.enonic.wem.core.schema.mixin.MixinServiceImpl;
import com.enonic.wem.core.schema.mixin.MixinsInitializer;
import com.enonic.wem.core.schema.mixin.dao.MixinDao;
import com.enonic.wem.core.schema.mixin.dao.MixinDaoImpl;
import com.enonic.wem.core.schema.relationship.CreateRelationshipTypeHandler;
import com.enonic.wem.core.schema.relationship.DeleteRelationshipTypeHandler;
import com.enonic.wem.core.schema.relationship.GetAllRelationshipTypesHandler;
import com.enonic.wem.core.schema.relationship.GetRelationshipTypeHandler;
import com.enonic.wem.core.schema.relationship.RelationshipTypesExistsHandler;
import com.enonic.wem.core.schema.relationship.RelationshipTypesInitializer;
import com.enonic.wem.core.schema.relationship.UpdateRelationshipTypeHandler;
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

        final InitializerTaskBinder tasks = InitializerTaskBinder.from( binder() );
        tasks.add( ContentTypesInitializer.class );
        tasks.add( MixinsInitializer.class );
        tasks.add( RelationshipTypesInitializer.class );
        tasks.add( DemoImagesInitializer.class );

        final CommandBinder commands = CommandBinder.from( binder() );

        commands.add( GetSchemasHandler.class );
        commands.add( GetRootSchemasHandler.class );
        commands.add( GetChildSchemasHandler.class );

        commands.add( CreateContentTypeHandler.class );
        commands.add( DeleteContentTypeHandler.class );
        commands.add( GetContentTypesHandler.class );
        commands.add( GetContentTypeHandler.class );
        commands.add( GetAllContentTypesHandler.class );
        commands.add( GetRootContentTypesHandler.class );
        commands.add( GetChildContentTypesHandler.class );
        commands.add( UpdateContentTypeHandler.class );
        commands.add( ValidateContentTypeHandler.class );

        commands.add( CreateRelationshipTypeHandler.class );
        commands.add( DeleteRelationshipTypeHandler.class );
        commands.add( GetAllRelationshipTypesHandler.class );
        commands.add( GetRelationshipTypeHandler.class );
        commands.add( RelationshipTypesExistsHandler.class );
        commands.add( UpdateRelationshipTypeHandler.class );
    }
}
