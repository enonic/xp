package com.enonic.wem.core.content;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

import com.enonic.wem.core.command.CommandBinder;
import com.enonic.wem.core.content.binary.CreateBinaryHandler;
import com.enonic.wem.core.content.binary.DeleteBinaryHandler;
import com.enonic.wem.core.content.binary.GetBinaryHandler;
import com.enonic.wem.core.content.binary.dao.BinaryDao;
import com.enonic.wem.core.content.binary.dao.BinaryDaoImpl;
import com.enonic.wem.core.content.dao.ContentDao;
import com.enonic.wem.core.content.dao.ContentDaoImpl;
import com.enonic.wem.core.initializer.InitializerTaskBinder;
import com.enonic.wem.core.relationship.CreateRelationshipHandler;
import com.enonic.wem.core.relationship.DeleteRelationshipHandler;
import com.enonic.wem.core.relationship.GetRelationshipsHandler;
import com.enonic.wem.core.relationship.RelationshipService;
import com.enonic.wem.core.relationship.RelationshipServiceImpl;
import com.enonic.wem.core.relationship.UpdateRelationshipHandler;
import com.enonic.wem.core.relationship.dao.RelationshipDao;
import com.enonic.wem.core.relationship.dao.RelationshipDaoImpl;
import com.enonic.wem.core.schema.GetSchemaTreeHandler;
import com.enonic.wem.core.schema.GetSchemasHandler;
import com.enonic.wem.core.schema.content.ContentTypesInitializer;
import com.enonic.wem.core.schema.content.CreateContentTypeHandler;
import com.enonic.wem.core.schema.content.DeleteContentTypeHandler;
import com.enonic.wem.core.schema.content.DemoImagesInitializer;
import com.enonic.wem.core.schema.content.GetContentTypeTreeHandler;
import com.enonic.wem.core.schema.content.GetContentTypesHandler;
import com.enonic.wem.core.schema.content.UpdateContentTypeHandler;
import com.enonic.wem.core.schema.content.ValidateContentTypeHandler;
import com.enonic.wem.core.schema.content.dao.ContentTypeDao;
import com.enonic.wem.core.schema.content.dao.ContentTypeDaoImpl;
import com.enonic.wem.core.schema.mixin.CreateMixinHandler;
import com.enonic.wem.core.schema.mixin.DeleteMixinHandler;
import com.enonic.wem.core.schema.mixin.GetMixinsHandler;
import com.enonic.wem.core.schema.mixin.MixinsInitializer;
import com.enonic.wem.core.schema.mixin.UpdateMixinHandler;
import com.enonic.wem.core.schema.mixin.dao.MixinDao;
import com.enonic.wem.core.schema.mixin.dao.MixinDaoImpl;
import com.enonic.wem.core.schema.relationship.CreateRelationshipTypeHandler;
import com.enonic.wem.core.schema.relationship.DeleteRelationshipTypeHandler;
import com.enonic.wem.core.schema.relationship.GetRelationshipTypesHandler;
import com.enonic.wem.core.schema.relationship.RelationshipTypesExistsHandler;
import com.enonic.wem.core.schema.relationship.RelationshipTypesInitializer;
import com.enonic.wem.core.schema.relationship.UpdateRelationshipTypesHandler;
import com.enonic.wem.core.schema.relationship.dao.RelationshipTypeDao;
import com.enonic.wem.core.schema.relationship.dao.RelationshipTypeDaoImpl;

public final class ContentModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind( ContentDao.class ).to( ContentDaoImpl.class ).in( Scopes.SINGLETON );
        bind( BinaryDao.class ).to( BinaryDaoImpl.class ).in( Scopes.SINGLETON );
        bind( RelationshipDao.class ).to( RelationshipDaoImpl.class ).in( Scopes.SINGLETON );
        bind( ContentTypeDao.class ).to( ContentTypeDaoImpl.class ).in( Scopes.SINGLETON );
        bind( MixinDao.class ).to( MixinDaoImpl.class ).in( Scopes.SINGLETON );
        bind( RelationshipTypeDao.class ).to( RelationshipTypeDaoImpl.class ).in( Scopes.SINGLETON );
        bind( RelationshipService.class ).to( RelationshipServiceImpl.class ).in( Scopes.SINGLETON );

        final InitializerTaskBinder tasks = InitializerTaskBinder.from( binder() );
        tasks.bind( ContentInitializer.class );
        tasks.bind( ContentTypesInitializer.class );
        tasks.bind( DemoImagesInitializer.class );
        tasks.bind( MixinsInitializer.class );
        tasks.bind( RelationshipTypesInitializer.class );

        final CommandBinder commands = CommandBinder.from( binder() );
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
        commands.add( ValidateContentDataHandler.class );

        commands.add( CreateBinaryHandler.class );
        commands.add( DeleteBinaryHandler.class );
        commands.add( GetBinaryHandler.class );

        commands.add( CreateRelationshipHandler.class );
        commands.add( DeleteRelationshipHandler.class );
        commands.add( GetRelationshipsHandler.class );
        commands.add( UpdateRelationshipHandler.class );

        commands.add( GetSchemasHandler.class );
        commands.add( GetSchemaTreeHandler.class );

        commands.add( CreateContentTypeHandler.class );
        commands.add( DeleteContentTypeHandler.class );
        commands.add( GetContentTypesHandler.class );
        commands.add( GetContentTypeTreeHandler.class );
        commands.add( UpdateContentTypeHandler.class );
        commands.add( ValidateContentTypeHandler.class );

        commands.add( CreateMixinHandler.class );
        commands.add( DeleteMixinHandler.class );
        commands.add( GetMixinsHandler.class );
        commands.add( UpdateMixinHandler.class );

        commands.add( CreateRelationshipTypeHandler.class );
        commands.add( DeleteRelationshipTypeHandler.class );
        commands.add( GetRelationshipTypesHandler.class );
        commands.add( RelationshipTypesExistsHandler.class );
        commands.add( UpdateRelationshipTypesHandler.class );
    }
}
