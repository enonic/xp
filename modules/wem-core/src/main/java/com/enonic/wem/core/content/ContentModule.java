package com.enonic.wem.core.content;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

import com.enonic.wem.core.command.CommandBinder;
import com.enonic.wem.core.content.attachment.CreateAttachmentHandler;
import com.enonic.wem.core.content.attachment.DeleteAttachmentHandler;
import com.enonic.wem.core.content.attachment.GetAttachmentHandler;
import com.enonic.wem.core.content.attachment.dao.AttachmentDao;
import com.enonic.wem.core.content.attachment.dao.AttachmentDaoImpl;
import com.enonic.wem.core.content.binary.CreateBinaryHandler;
import com.enonic.wem.core.content.binary.DeleteBinaryHandler;
import com.enonic.wem.core.content.binary.GetBinaryHandler;
import com.enonic.wem.core.content.binary.dao.BinaryDao;
import com.enonic.wem.core.content.binary.dao.BinaryDaoImpl;
import com.enonic.wem.core.content.dao.ContentDao;
import com.enonic.wem.core.content.dao.ContentDaoImpl;
import com.enonic.wem.core.initializer.InitializerTaskBinder;

public final class ContentModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind( ContentDao.class ).to( ContentDaoImpl.class ).in( Scopes.SINGLETON );
        bind( BinaryDao.class ).to( BinaryDaoImpl.class ).in( Scopes.SINGLETON );
        bind( AttachmentDao.class ).to( AttachmentDaoImpl.class ).in( Scopes.SINGLETON );

        final InitializerTaskBinder tasks = InitializerTaskBinder.from( binder() );
        tasks.bind( ContentInitializer.class );

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

        commands.add( CreateAttachmentHandler.class );
        commands.add( DeleteAttachmentHandler.class );
        commands.add( GetAttachmentHandler.class );
    }
}
