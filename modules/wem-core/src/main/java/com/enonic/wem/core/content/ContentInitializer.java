package com.enonic.wem.core.content;


import javax.inject.Inject;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.CreateContentParams;
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

    private ContentService contentService;

    protected ContentInitializer()
    {
        super( 15, "content" );
    }

    @Override
    public void initialize()
        throws Exception
    {
        ContentPath imageArchivePath = contentService.create( createFolder().
            name( "imagearchive" ).
            parent( ContentPath.ROOT ).
            displayName( "Image Archive" ) ).getPath();

        contentService.create( createFolder().
            name( "misc" ).
            parent( imageArchivePath ).
            displayName( "Misc" ) ).getPath();

        contentService.create( createFolder().
            name( "people" ).
            parent( imageArchivePath ).
            displayName( "People" ) ).getPath();

        ContentPath trampolinerPath = contentService.create( createFolder().
            name( "trampoliner" ).
            parent( imageArchivePath ).
            displayName( "Trampoliner" ) ).getPath();

        contentService.create( createFolder().
            name( "jumping-jack-big-bounce" ).
            parent( trampolinerPath ).
            displayName( "Jumping Jack - Big Bounce" ) ).getPath();

        contentService.create( createFolder().
            name( "jumping-jack-pop" ).
            parent( trampolinerPath ).
            displayName( "Jumping Jack - Pop" ).
            contentType( ContentTypeName.folder() ) ).getPath();
    }

    private CreateContentParams createFolder()
    {
        return new CreateContentParams().
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

    @Inject
    public void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
    }
}
