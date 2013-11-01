package com.enonic.wem.core.schema.content;

import javax.inject.Inject;

import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.command.schema.content.GetRootContentTypes;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.schema.content.dao.ContentTypeDao;


public class GetRootContentTypesHandler
    extends CommandHandler<GetRootContentTypes>
{
    private ContentTypeDao contentTypeDao;

    @Override
    public void handle()
        throws Exception
    {
        ImmutableList.Builder<ContentType> rootContentTypes = ImmutableList.builder();

        final ContentTypes contentTypes = contentTypeDao.selectAll( context.getJcrSession() );
        for ( ContentType contentType : contentTypes )
        {
            if ( contentType.getSuperType() == null )
            {
                rootContentTypes.add( contentType );
            }
        }
        command.setResult( ContentTypes.from( rootContentTypes.build() ) );
    }

    @Inject
    public void setContentTypeDao( final ContentTypeDao contentTypeDao )
    {
        this.contentTypeDao = contentTypeDao;
    }
}
