package com.enonic.wem.core.content;


import javax.inject.Inject;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.content.ContentConstants;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.CreateContentParams;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.api.schema.content.GetContentTypesParams;
import com.enonic.wem.core.support.BaseInitializer;


public class ContentInitializer
    extends BaseInitializer
{

    public static final String IMAGE_ARCHIVE_PATH_ELEMENT = "imagearchive";

    public static final String TRAMPOLINE_PATH_ELEMENT = "trampoliner";

    public static final String JUMPING_JACK_BIG_BOUNCE_PATH_ELEMENT = "jumping-jack-big-bounce";

    public static final String JUMPING_JACK_POP_PATH_ELEMENT = "jumping-jack-pop";

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
        final Context context = ContentConstants.DEFAULT_CONTEXT;

        ContentPath imageArchivePath = contentService.create( createFolder().
            name( IMAGE_ARCHIVE_PATH_ELEMENT ).
            parent( ContentPath.ROOT ).
            displayName( "Image Archive" ), context ).getPath();

        contentService.create( createFolder().
            name( "misc" ).
            parent( imageArchivePath ).
            displayName( "Misc" ), context ).getPath();

        contentService.create( createFolder().
            name( "people" ).
            parent( imageArchivePath ).
            displayName( "People" ), context ).getPath();

        ContentPath trampolinerPath = contentService.create( createFolder().
            name( TRAMPOLINE_PATH_ELEMENT ).
            parent( imageArchivePath ).
            displayName( "Trampoliner" ), context ).getPath();

        contentService.create( createFolder().
            name( JUMPING_JACK_BIG_BOUNCE_PATH_ELEMENT ).
            parent( trampolinerPath ).
            displayName( "Jumping Jack - Big Bounce" ), context ).getPath();

        contentService.create( createFolder().
            name( JUMPING_JACK_POP_PATH_ELEMENT ).
            parent( trampolinerPath ).
            displayName( "Jumping Jack - Pop" ).
            contentType( ContentTypeName.folder() ), context ).getPath();
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
