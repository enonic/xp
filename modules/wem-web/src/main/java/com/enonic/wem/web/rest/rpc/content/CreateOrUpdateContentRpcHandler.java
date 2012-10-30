package com.enonic.wem.web.rest.rpc.content;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.CreateContent;
import com.enonic.wem.api.command.content.UpdateContents;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentPaths;
import com.enonic.wem.api.content.Contents;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.content.editor.ContentEditors;
import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.ContentTypeFetcher;
import com.enonic.wem.api.content.type.MockContentTypeFetcher;
import com.enonic.wem.api.content.type.QualifiedContentTypeName;
import com.enonic.wem.api.content.type.formitem.FormItemSet;
import com.enonic.wem.api.content.type.formitem.inputtype.InputTypes;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.web.json.rpc.JsonRpcContext;
import com.enonic.wem.web.rest.rpc.AbstractDataRpcHandler;
import com.enonic.wem.web.rest.service.upload.UploadService;

import static com.enonic.wem.api.content.type.formitem.FormItemSet.newFormItemSet;
import static com.enonic.wem.api.content.type.formitem.Input.newInput;

@Component
public final class CreateOrUpdateContentRpcHandler
    extends AbstractDataRpcHandler
{
    private UploadService uploadService;

    private ContentTypeFetcher contentTypeFetcher;

    public CreateOrUpdateContentRpcHandler()
    {
        super( "content_createOrUpdate" );

        MockContentTypeFetcher mockContentTypeFetcher = new MockContentTypeFetcher();
        ContentType myContentType = new ContentType();
        myContentType.setModule( Module.newModule().name( "myModule" ).build() );
        myContentType.setName( "myContentType" );
        myContentType.addFormItem( newInput().name( "myTextLine1" ).type( InputTypes.TEXT_LINE ).build() );
        myContentType.addFormItem( newInput().name( "myTextLine2" ).type( InputTypes.TEXT_LINE ).build() );
        FormItemSet formItemSet = newFormItemSet().name( "myFormItemSet" ).build();
        formItemSet.addFormItem( newInput().name( "myTextLine1" ).type( InputTypes.TEXT_LINE ).build() );
        myContentType.addFormItem( formItemSet );
        mockContentTypeFetcher.add( myContentType );
        this.contentTypeFetcher = mockContentTypeFetcher;
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final QualifiedContentTypeName qualifiedContentTypeName =
            new QualifiedContentTypeName( context.param( "qualifiedContentTypeName" ).required().asString() );
        final ContentPath contentPath = ContentPath.from( context.param( "contentPath" ).required().asString() );

        final ContentType contentType = contentTypeFetcher.getContentType( qualifiedContentTypeName );
        final ContentData contentData = new ContentDataParser( contentType ).parse( context.param( "contentData" ).required().asObject() );

        if ( !contentExists( contentPath ) )
        {
            final CreateContent createContent = Commands.content().create();
            createContent.contentPath( contentPath );
            createContent.contentType( qualifiedContentTypeName );
            createContent.contentData( contentData );
            client.execute( createContent );
            context.setResult( CreateOrUpdateContentJsonResult.created() );
        }
        else
        {
            final UpdateContents updateContents = Commands.content().update();
            updateContents.paths( ContentPaths.from( contentPath ) );
            updateContents.editor( ContentEditors.setContentData( contentData ) );
            client.execute( updateContents );
            context.setResult( CreateOrUpdateContentJsonResult.updated() );
        }
    }

    private boolean contentExists( final ContentPath contentPath )
    {
        final Contents contents = client.execute( Commands.content().get().paths( ContentPaths.from( contentPath ) ) );
        return contents.isNotEmpty();
    }

    @Autowired
    public void setUploadService( final UploadService uploadService )
    {
        this.uploadService = uploadService;
    }
}
