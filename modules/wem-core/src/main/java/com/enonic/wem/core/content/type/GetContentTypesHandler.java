package com.enonic.wem.core.content.type;

import javax.jcr.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.content.type.GetContentTypes;
import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.ContentTypes;
import com.enonic.wem.api.content.type.QualifiedContentTypeNames;
import com.enonic.wem.api.content.type.form.MixinFetcher;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.type.dao.ContentTypeDao;
import com.enonic.wem.core.content.type.dao.MixinDao;

@Component
public final class GetContentTypesHandler
    extends CommandHandler<GetContentTypes>
{
    private ContentTypeDao contentTypeDao;

    private MixinDao mixinDao;

    public GetContentTypesHandler()
    {
        super( GetContentTypes.class );
    }

    @Override
    public void handle( final CommandContext context, final GetContentTypes command )
        throws Exception
    {
        final Session session = context.getJcrSession();
        final ContentTypes contentTypes;
        if ( command.isGetAll() )
        {
            contentTypes = getAllContentTypes( session );
        }
        else
        {
            final QualifiedContentTypeNames contentTypeNames = command.getNames();
            contentTypes = getContentTypes( session, contentTypeNames );
        }

        if ( command.isMixinReferencesToFormItems() )
        {
            final MixinFetcher mixinFetcher = new InternalMixinFetcher( mixinDao, session );
            for ( ContentType contentType : contentTypes )
            {
                contentType.form().mixinReferencesToFormItems( mixinFetcher );
            }
        }

        command.setResult( contentTypes );
    }

    private ContentTypes getAllContentTypes( final Session session )
    {
        return contentTypeDao.retrieveAllContentTypes( session );
    }

    private ContentTypes getContentTypes( final Session session, final QualifiedContentTypeNames contentTypeNames )
    {
        return contentTypeDao.retrieveContentTypes( contentTypeNames, session );
    }

    @Autowired
    public void setContentTypeDao( final ContentTypeDao contentTypeDao )
    {
        this.contentTypeDao = contentTypeDao;
    }

    @Autowired
    public void setMixinDao( final MixinDao mixinDao )
    {
        this.mixinDao = mixinDao;
    }
}
