package com.enonic.wem.core.content;


import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.CreateContent;
import com.enonic.wem.api.command.content.CreateContentResult;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.api.space.SpaceName;
import com.enonic.wem.core.support.BaseInitializer;


public class ContentInitializer
    extends BaseInitializer
{

    protected ContentInitializer()
    {
        super( 15, "content" );
    }

    @Override
    public void initialize()
        throws Exception
    {
        final SpaceName space = SpaceName.from( "bildearkiv" );
        createContent( ContentPath.rootOf( space ), "Misc", ContentTypeName.folder() );
        createContent( ContentPath.rootOf( space ), "People", ContentTypeName.folder() );
        final ContentPath trampolinerContent = createContent( ContentPath.rootOf( space ), "Trampoliner", ContentTypeName.folder() );
        createContent( trampolinerContent, "Jumping Jack - Big Bounce", ContentTypeName.folder() );
        createContent( trampolinerContent, "Jumping Jack - Pop", ContentTypeName.folder() );
    }

    private ContentPath createContent( final ContentPath parent, final String displayName, ContentTypeName contentTypeName )
    {
        final CreateContent createContent = Commands.content().create().
            contentType( contentTypeName ).
            form( getContentType( contentTypeName ).form() ).
            displayName( displayName ).
            parentContentPath( parent ).
            owner( AccountKey.anonymous() ).
            contentData( new ContentData() );
        CreateContentResult createContentResult = client.execute( createContent );
        return createContentResult.getContentPath();
    }


    private ContentType getContentType( ContentTypeName name )
    {
        return this.client.execute( Commands.contentType().get().byNames().contentTypeNames( ContentTypeNames.from( name ) ) ).first();
    }

}
