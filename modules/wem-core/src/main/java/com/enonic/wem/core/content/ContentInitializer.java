package com.enonic.wem.core.content;


import javax.inject.Inject;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.CreateContent;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.api.schema.content.GetContentTypesParams;
import com.enonic.wem.core.support.BaseInitializer;


public class ContentInitializer
    extends BaseInitializer
{
    private ContentTypeService contentTypeService;

    protected ContentInitializer()
    {
        super( 15, "content" );
    }

    @Override
    public void initialize()
        throws Exception
    {
        ContentPath bildeArkivPath = client.execute( createFolder().
            name( "bildearkiv" ).
            parent( ContentPath.ROOT ).
            displayName( "BildeArkiv" ) ).getPath();

        client.execute( createFolder().
            name( "misc" ).
            parent( bildeArkivPath ).
            displayName( "Misc" ) ).getPath();

        client.execute( createFolder().
            name( "people" ).
            parent( bildeArkivPath ).
            displayName( "People" ) ).getPath();

        ContentPath trampolinerPath = client.execute( createFolder().
            name( "trampoliner" ).
            parent( bildeArkivPath ).
            displayName( "Trampoliner" ) ).getPath();

        client.execute( createFolder().
            name( "jumping-jack-big-bounce" ).
            parent( trampolinerPath ).
            displayName( "Jumping Jack - Big Bounce" ) ).getPath();

        client.execute( createFolder().
            name( "jumping-jack-pop" ).
            parent( trampolinerPath ).
            displayName( "Jumping Jack - Pop" ).
            contentType( ContentTypeName.folder() ) ).getPath();
    }

    private CreateContent createFolder()
    {
        return Commands.content().create().
            owner( AccountKey.anonymous() ).
            contentData( new ContentData() ).
            form( getContentType( ContentTypeName.folder() ).form() ).
            contentType( ContentTypeName.folder() );
    }

    private ContentType getContentType( ContentTypeName name )
    {
        final GetContentTypesParams params = new GetContentTypesParams().contentTypeNames( ContentTypeNames.from( name ) );

        return contentTypeService.getByNames( params ).first();
    }

    @Inject
    public void setContentTypeService( final ContentTypeService contentTypeService )
    {
        this.contentTypeService = contentTypeService;
    }
}
