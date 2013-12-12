package com.enonic.wem.core.content;


import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.CreateContent;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;
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
        ContentPath bildeArkivPath = client.execute( createFolder().
            name( "bildearkiv" ).
            parent( ContentPath.ROOT ).
            displayName( "BildeArkiv" ) ).getContentPath();

        client.execute( createFolder().
            name( "misc" ).
            parent( bildeArkivPath ).
            displayName( "Misc" ) ).getContentPath();

        client.execute( createFolder().
            name( "people" ).
            parent( bildeArkivPath ).
            displayName( "People" ) ).getContentPath();

        ContentPath trampolinerPath = client.execute( createFolder().
            name( "trampoliner" ).
            parent( bildeArkivPath ).
            displayName( "Trampoliner" ) ).getContentPath();

        client.execute( createFolder().
            name( "jumping-jack-big-bounce" ).
            parent( trampolinerPath ).
            displayName( "Jumping Jack - Big Bounce" ) ).getContentPath();

        client.execute( createFolder().
            name( "jumping-jack-pop" ).
            parent( trampolinerPath ).
            displayName( "Jumping Jack - Pop" ).
            contentType( ContentTypeName.folder() ) ).getContentPath();
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
        return this.client.execute( Commands.contentType().get().byNames().contentTypeNames( ContentTypeNames.from( name ) ) ).first();
    }

}
