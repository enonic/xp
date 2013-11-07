package com.enonic.wem.core.schema.content;

import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.schema.content.GetChildContentTypes;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.core.command.CommandHandler;


public class GetChildContentTypesHandler
    extends CommandHandler<GetChildContentTypes>
{
    @Override
    public void handle()
        throws Exception
    {
        ImmutableList.Builder<ContentType> childContentTypes = ImmutableList.builder();

        final ContentTypeName name = command.getParentName();

        final ContentTypes contentTypes = context.getClient().execute( Commands.contentType().get().all() );

        for ( ContentType contentType : contentTypes )
        {
            if ( name.equals( contentType.getSuperType() ) )
            {
                childContentTypes.add( contentType );
            }
        }
        command.setResult( ContentTypes.from( childContentTypes.build() ) );
    }

}
