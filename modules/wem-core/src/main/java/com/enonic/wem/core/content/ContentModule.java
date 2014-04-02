package com.enonic.wem.core.content;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;

import com.enonic.wem.api.command.content.attachment.AttachmentService;
import com.enonic.wem.core.command.CommandBinder;
import com.enonic.wem.core.content.attachment.AttachmentServiceImpl;
import com.enonic.wem.core.initializer.InitializerTaskBinder;

public final class ContentModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind( AttachmentService.class ).to( AttachmentServiceImpl.class ).in( Singleton.class );

        final InitializerTaskBinder tasks = InitializerTaskBinder.from( binder() );
        tasks.add( ContentInitializer.class );

        final CommandBinder commands = CommandBinder.from( binder() );
        commands.add( CreateContentHandler.class );
        commands.add( DeleteContentHandler.class );
        commands.add( FindContentHandler.class );
        commands.add( GenerateContentNameHandler.class );
        commands.add( GetChildContentHandler.class );
        commands.add( GetContentByIdHandler.class );
        commands.add( GetContentByIdsHandler.class );
        commands.add( GetContentByPathHandler.class );
        commands.add( GetContentByPathsHandler.class );
        commands.add( GetRootContentHandler.class );
        commands.add( RenameContentHandler.class );
        commands.add( UpdateContentHandler.class );
        commands.add( ValidateContentDataHandler.class );
    }
}
