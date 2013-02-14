package com.enonic.wem.core.content;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.content.UpdateContents;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.content.editor.ContentEditor;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.dao.ContentDao;
import com.enonic.wem.core.search.IndexService;

import static com.enonic.wem.api.content.Content.newContent;

@Component
public class UpdateContentsHandler
    extends CommandHandler<UpdateContents>
{
    private ContentDao contentDao;

    private IndexService indexService;

    private final static Logger LOG = LoggerFactory.getLogger( UpdateContentsHandler.class );


    public UpdateContentsHandler()
    {
        super( UpdateContents.class );
    }

    @Override
    public void handle( final CommandContext context, final UpdateContents command )
        throws Exception
    {
        final Contents contents = contentDao.select( command.getSelectors(), context.getJcrSession() );
        for ( Content contentToUpdate : contents )
        {
            ContentEditor contentEditor = command.getEditor();
            final Content modifiedContent = contentEditor.edit( contentToUpdate );
            if ( modifiedContent != null )
            {
                contentToUpdate = newContent( modifiedContent ).
                    modifiedTime( DateTime.now() ).
                    modifier( command.getModifier() ).build();
                final boolean createNewVersion = true;
                contentDao.update( contentToUpdate, createNewVersion, context.getJcrSession() );
                context.getJcrSession().save();

                try
                {
                    // TODO: Temporary easy solution. The index logic should eventually not be here anyway
                    indexService.indexContent( contentToUpdate );
                }
                catch ( Exception e )
                {
                    LOG.error( "Index content failed", e );
                }

            }
        }
    }

    @Autowired
    public void setContentDao( final ContentDao contentDao )
    {
        this.contentDao = contentDao;
    }

    @Autowired
    public void setIndexService( final IndexService indexService )
    {
        this.indexService = indexService;
    }
}
