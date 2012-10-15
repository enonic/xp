package com.enonic.wem.core.content;

import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.content.UpdateContent;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.content.editor.ContentEditor;
import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.QualifiedContentTypeName;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;

@Component
public class UpdateContentHandler
    extends CommandHandler<UpdateContent>
{
    private MockContentDao contentDao = MockContentDao.get();

    private MockContentTypeDao contentTypeDao = MockContentTypeDao.get();

    public UpdateContentHandler()
    {
        super( UpdateContent.class );
    }

    @Override
    public void handle( final CommandContext context, final UpdateContent command )
        throws Exception
    {
        final Content content = contentDao.getContentByPath( command.getContentPath() );

        final ContentData contentData = command.getContentData();
        if ( contentData != null )
        {
            content.setData( contentData );
        }

        final QualifiedContentTypeName qualifiedContentTypeName = command.getContentType();
        if ( qualifiedContentTypeName != null )
        {
            final ContentType contentType = contentTypeDao.getContentType( qualifiedContentTypeName );
            content.setType( contentType );
        }

        ContentEditor editor = command.getContentEditor();
        editor.edit( content );

        System.out.println( "Content updated" );
    }

}
