package com.enonic.wem.core.content.type;

import javax.jcr.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.content.type.UpdateContentTypes;
import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.QualifiedContentTypeNames;
import com.enonic.wem.api.content.type.ContentTypes;
import com.enonic.wem.api.content.type.QualifiedContentTypeName;
import com.enonic.wem.api.content.type.editor.ContentTypeEditor;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.type.dao.ContentTypeDao;

@Component
public final class UpdateContentTypesHandler
    extends CommandHandler<UpdateContentTypes>
{
    private ContentTypeDao contentTypeDao;

    public UpdateContentTypesHandler()
    {
        super( UpdateContentTypes.class );
    }

    @Override
    public void handle( final CommandContext context, final UpdateContentTypes command )
        throws Exception
    {
        final QualifiedContentTypeNames contentTypeNames = command.getNames();
        final ContentTypeEditor editor = command.getEditor();
        final Session session = context.getJcrSession();
        int contentTypesUpdated = 0;
        for ( QualifiedContentTypeName contentTypeName : contentTypeNames )
        {
            final ContentType contentType = retrieveContentType( session, contentTypeName );
            if ( contentType != null )
            {
                final ContentType modifiedContentType = editor.edit( contentType );
                if ( modifiedContentType != null )
                {
                    updateContentType( session, contentType );
                    contentTypesUpdated++;
                }
            }
        }

        session.save();
        command.setResult( contentTypesUpdated );
    }

    private void updateContentType( final Session session, final ContentType contentType )
    {
        contentTypeDao.updateContentType( session, contentType );
    }

    private ContentType retrieveContentType( final Session session, final QualifiedContentTypeName contentTypeName )
    {
        final ContentTypes contentTypes = contentTypeDao.retrieveContentTypes( session, QualifiedContentTypeNames.from( contentTypeName ) );
        return contentTypes.isEmpty() ? null : contentTypes.getFirst();
    }

    @Autowired
    public void setContentTypeDao( final ContentTypeDao contentTypeDao )
    {
        this.contentTypeDao = contentTypeDao;
    }
}
