package com.enonic.wem.core.content;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

import com.enonic.wem.core.command.CommandBinder;
import com.enonic.wem.core.content.dao.ContentDao;
import com.enonic.wem.core.content.dao.ContentDaoImpl;
import com.enonic.wem.core.content.relationship.CreateRelationshipHandler;
import com.enonic.wem.core.content.relationship.DeleteRelationshipHandler;
import com.enonic.wem.core.content.relationship.RelationshipService;
import com.enonic.wem.core.content.relationship.RelationshipServiceImpl;
import com.enonic.wem.core.content.relationship.UpdateRelationshipHandler;
import com.enonic.wem.core.content.relationship.dao.RelationshipDao;
import com.enonic.wem.core.content.relationship.dao.RelationshipDaoImpl;
import com.enonic.wem.core.content.schema.GetSchemaTreeHandler;
import com.enonic.wem.core.content.schema.GetSchemasHandler;
import com.enonic.wem.core.content.schema.content.ContentTypesInitializer;
import com.enonic.wem.core.content.schema.content.CreateContentTypeHandler;
import com.enonic.wem.core.content.schema.content.DeleteContentTypeHandler;
import com.enonic.wem.core.content.schema.content.GetContentTypeTreeHandler;
import com.enonic.wem.core.content.schema.content.GetContentTypesHandler;
import com.enonic.wem.core.content.schema.content.UpdateContentTypeHandler;
import com.enonic.wem.core.content.schema.content.ValidateContentTypeHandler;
import com.enonic.wem.core.content.schema.content.dao.ContentTypeDao;
import com.enonic.wem.core.content.schema.content.dao.ContentTypeDaoImpl;
import com.enonic.wem.core.content.schema.mixin.CreateMixinHandler;
import com.enonic.wem.core.content.schema.mixin.DeleteMixinHandler;
import com.enonic.wem.core.content.schema.mixin.GetMixinsHandler;
import com.enonic.wem.core.content.schema.mixin.UpdateMixinHandler;
import com.enonic.wem.core.content.schema.mixin.dao.MixinDao;
import com.enonic.wem.core.content.schema.mixin.dao.MixinDaoImpl;
import com.enonic.wem.core.content.schema.relationship.CreateRelationshipTypeHandler;
import com.enonic.wem.core.content.schema.relationship.DeleteRelationshipTypeHandler;
import com.enonic.wem.core.content.schema.relationship.GetRelationshipTypesHandler;
import com.enonic.wem.core.content.schema.relationship.RelationshipTypesExistsHandler;
import com.enonic.wem.core.content.schema.relationship.RelationshipTypesInitializer;
import com.enonic.wem.core.content.schema.relationship.UpdateRelationshipTypesHandler;
import com.enonic.wem.core.content.schema.relationship.dao.RelationshipTypeDao;
import com.enonic.wem.core.content.schema.relationship.dao.RelationshipTypeDaoImpl;
import com.enonic.wem.core.initializer.InitializerBinder;

public final class ContentModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        final CommandBinder commands = CommandBinder.from( binder() );
        final InitializerBinder initializers = InitializerBinder.from( binder() );

        bind( ContentDao.class ).to( ContentDaoImpl.class ).in( Scopes.SINGLETON );
        bind( RelationshipDao.class ).to( RelationshipDaoImpl.class ).in( Scopes.SINGLETON );
        bind( RelationshipTypeDao.class ).to( RelationshipTypeDaoImpl.class ).in( Scopes.SINGLETON );
        bind( MixinDao.class ).to( MixinDaoImpl.class ).in( Scopes.SINGLETON );
        bind( ContentTypeDao.class ).to( ContentTypeDaoImpl.class ).in( Scopes.SINGLETON );
        bind( RelationshipService.class ).to( RelationshipServiceImpl.class ).in( Scopes.SINGLETON );

        commands.add( CreateContentHandler.class );
        commands.add( DeleteContentHandler.class );
        commands.add( FindContentHandler.class );
        commands.add( GenerateContentNameHandler.class );
        commands.add( GetChildContentHandler.class );
        commands.add( GetContentsHandler.class );
        commands.add( GetContentTreeHandler.class );
        commands.add( GetContentVersionHandler.class );
        commands.add( GetContentVersionHistoryHandler.class );
        commands.add( RenameContentHandler.class );
        commands.add( UpdateContentHandler.class );
        commands.add( ValidateRootDataSetHandler.class );
        commands.add( GetSchemasHandler.class );
        commands.add( GetSchemaTreeHandler.class );
        commands.add( CreateRelationshipHandler.class );
        commands.add( DeleteRelationshipHandler.class );
        commands.add( UpdateRelationshipHandler.class );
        commands.add( CreateRelationshipTypeHandler.class );
        commands.add( DeleteRelationshipTypeHandler.class );
        commands.add( GetRelationshipTypesHandler.class );
        commands.add( RelationshipTypesExistsHandler.class );
        commands.add( UpdateRelationshipTypesHandler.class );
        commands.add( CreateMixinHandler.class );
        commands.add( DeleteMixinHandler.class );
        commands.add( GetMixinsHandler.class );
        commands.add( UpdateMixinHandler.class );
        commands.add( CreateContentTypeHandler.class );
        commands.add( DeleteContentTypeHandler.class );
        commands.add( GetContentTypesHandler.class );
        commands.add( GetContentTypeTreeHandler.class );
        commands.add( UpdateContentTypeHandler.class );
        commands.add( ValidateContentTypeHandler.class );

        initializers.add( RelationshipTypesInitializer.class );
        initializers.add( ContentTypesInitializer.class );
    }
}
