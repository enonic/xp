package com.enonic.wem.core.content;


import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.CreateContent;
import com.enonic.wem.api.command.content.CreateContentResult;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.data.ContentData;
import com.enonic.wem.api.schema.content.QualifiedContentTypeName;
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
        createContent( ContentPath.rootOf( space ), "Misc", QualifiedContentTypeName.folder() );
        createContent( ContentPath.rootOf( space ), "People", QualifiedContentTypeName.folder() );
        final ContentPath trampolinerContent =
            createContent( ContentPath.rootOf( space ), "Trampoliner", QualifiedContentTypeName.folder() );
        createContent( trampolinerContent, "Jumping Jack - Big Bounce", QualifiedContentTypeName.folder() );
        createContent( trampolinerContent, "Jumping Jack - Pop", QualifiedContentTypeName.folder() );
    }

    private ContentPath createContent( final ContentPath parent, final String displayName, QualifiedContentTypeName contentTypeName )
    {
        final CreateContent createContent = Commands.content().create().
            contentType( contentTypeName ).
            displayName( displayName ).
            parentContentPath( parent ).
            owner( AccountKey.anonymous() ).
            contentData( new ContentData() );
        CreateContentResult createContentResult = client.execute( createContent );
        return createContentResult.getContentPath();
    }

}
