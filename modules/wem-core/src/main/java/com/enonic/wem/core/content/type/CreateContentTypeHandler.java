package com.enonic.wem.core.content.type;

import javax.jcr.Session;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.content.type.CreateContentType;
import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.type.dao.ContentTypeDao;

import static com.enonic.wem.api.content.type.ContentType.newContentType;

@Component
public final class CreateContentTypeHandler
    extends CommandHandler<CreateContentType>
{
    private ContentTypeDao contentTypeDao;

    public CreateContentTypeHandler()
    {
        super( CreateContentType.class );
    }

    @Override
    public void handle( final CommandContext context, final CreateContentType command )
        throws Exception
    {
        final ContentType.Builder builder = newContentType();
        builder.name( command.getName() );
        builder.displayName( command.getDisplayName() );
        builder.module( command.getModuleName() );
        builder.superType( command.getSuperType() );
        builder.setAbstract( command.isAbstract() );
        builder.setFinal( command.isFinal() );
        builder.icon( command.getIcon() );
        builder.createdTime( DateTime.now() );
        builder.modifiedTime( DateTime.now() );

        final ContentType contentType = builder.build();

        final Session session = context.getJcrSession();
        contentTypeDao.create( contentType, session );
        session.save();

        command.setResult( contentType.getQualifiedName() );
    }

    @Autowired
    public void setContentTypeDao( final ContentTypeDao contentTypeDao )
    {
        this.contentTypeDao = contentTypeDao;
    }
}
