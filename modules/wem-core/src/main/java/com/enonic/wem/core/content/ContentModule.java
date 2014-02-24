package com.enonic.wem.core.content;

import com.google.inject.AbstractModule;

import com.enonic.wem.core.command.CommandBinder;
import com.enonic.wem.core.content.attachment.GetAttachmentHandler;
import com.enonic.wem.core.content.attachment.GetAttachmentsHandler;
import com.enonic.wem.core.initializer.InitializerTaskBinder;

public final class ContentModule
    extends AbstractModule
{
    @Override
    protected void configure()
    {
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

        commands.add( GetAttachmentHandler.class );
        commands.add( GetAttachmentsHandler.class );
    }
}
