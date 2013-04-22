package com.enonic.wem.web.rest.rpc.content;


import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.CreateContent;
import com.enonic.wem.api.command.content.CreateContentResult;
import com.enonic.wem.api.command.content.UpdateContent;
import com.enonic.wem.api.command.content.UpdateContentResult;
import com.enonic.wem.api.command.content.schema.content.GetContentTypes;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.CreateContentException;
import com.enonic.wem.api.content.RenameContentException;
import com.enonic.wem.api.content.UpdateContentException;
import com.enonic.wem.api.content.data.RootDataSet;
import com.enonic.wem.api.content.schema.content.ContentType;
import com.enonic.wem.api.content.schema.content.QualifiedContentTypeName;
import com.enonic.wem.api.content.schema.content.QualifiedContentTypeNames;
import com.enonic.wem.web.json.JsonErrorResult;
import com.enonic.wem.web.json.rpc.JsonRpcContext;
import com.enonic.wem.web.json.rpc.JsonRpcException;
import com.enonic.wem.web.rest.rpc.AbstractDataRpcHandler;

import static com.enonic.wem.api.command.Commands.content;
import static com.enonic.wem.api.content.editor.ContentEditors.composite;
import static com.enonic.wem.api.content.editor.ContentEditors.setContentData;
import static com.enonic.wem.api.content.editor.ContentEditors.setContentDisplayName;

@Component
public final class CreateOrUpdateContentRpcHandler
    extends AbstractDataRpcHandler
{

    public CreateOrUpdateContentRpcHandler()
    {
        super( "content_createOrUpdate" );
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final ContentId contentId = contentIdOrNull( context.param( "contentId" ).asString() );
        if ( contentId == null )
        {
            HandleCreateContent handleCreateContent = new HandleCreateContent();
            handleCreateContent.context = context;
            handleCreateContent.handleCreateContent();
        }
        else
        {

            HandleUpdateContent handleUpdateContent = new HandleUpdateContent();
            handleUpdateContent.context = context;
            handleUpdateContent.contentId = contentId;
            handleUpdateContent.updateContent();
        }
    }

    private class HandleCreateContent
    {
        private JsonRpcContext context;

        private void handleCreateContent()
        {
            ContentPath parentContentPath;
            try
            {
                final boolean temporary = context.param( "temporary" ).asBoolean( false );
                final String contentName = context.param( "contentName" ).asString();
                parentContentPath = contentPathOrNull( context.param( "parentContentPath" ).asString() );
                if ( parentContentPath == null && !temporary )
                {
                    context.setResult( new JsonErrorResult( "Missing parameter [contentId] or [parentContentPath]" ) );
                    return;
                }

                final QualifiedContentTypeName qualifiedContentTypeName =
                    new QualifiedContentTypeName( context.param( "qualifiedContentTypeName" ).required().asString() );
                final ContentType contentType = getContentType( qualifiedContentTypeName );
                final RootDataSet rootDataSet =
                    new RootDataSetParser( contentType ).parse( context.param( "contentData" ).required().asObject() );
                final String displayName = context.param( "displayName" ).notBlank().asString();

                final CreateContent createContent = content().create().
                    parentContentPath( parentContentPath ).
                    name( contentName ).
                    contentType( qualifiedContentTypeName ).
                    rootDataSet( rootDataSet ).
                    displayName( displayName ).
                    owner( AccountKey.anonymous() ).
                    temporary( temporary );
                final CreateContentResult createContentResult = client.execute( createContent );

                context.setResult(
                    CreateOrUpdateContentJsonResult.created( createContentResult.getContentId(), createContentResult.getContentPath() ) );
            }
            catch ( CreateContentException | JsonRpcException e )
            {
                context.setResult( new JsonErrorResult( e.getMessage() ) );
            }
        }
    }

    private class HandleUpdateContent
    {
        private JsonRpcContext context;

        private ContentId contentId;

        private void updateContent()
        {
            try
            {
                final String displayName = context.param( "displayName" ).notBlank().asString();
                final QualifiedContentTypeName qualifiedContentTypeName =
                    new QualifiedContentTypeName( context.param( "qualifiedContentTypeName" ).required().asString() );
                final ContentType contentType = getContentType( qualifiedContentTypeName );
                final RootDataSet rootDataSet =
                    new RootDataSetParser( contentType ).parse( context.param( "contentData" ).required().asObject() );

                final UpdateContent updateContent = content().update();
                updateContent.selector( contentId );
                updateContent.editor( composite( setContentData( rootDataSet ), setContentDisplayName( displayName ) ) );
                updateContent.modifier( AccountKey.anonymous() );

                final UpdateContentResult result = client.execute( updateContent );
                context.setResult( CreateOrUpdateContentJsonResult.from( result ) );

                final String newContentName = context.param( "contentName" ).asString();
                final boolean renameContent = !Strings.isNullOrEmpty( newContentName );
                if ( renameContent )
                {
                    client.execute( content().rename().contentId( contentId ).newName( newContentName ) );
                }
            }
            catch ( UpdateContentException | RenameContentException | JsonRpcException e )
            {
                context.setResult( new JsonErrorResult( e.getMessage() ) );
            }
        }
    }

    private ContentType getContentType( final QualifiedContentTypeName qualifiedContentTypeName )
    {
        final GetContentTypes getContentTypes =
            Commands.contentType().get().qualifiedNames( QualifiedContentTypeNames.from( qualifiedContentTypeName ) );
        getContentTypes.mixinReferencesToFormItems( true );
        final ContentType contentType = client.execute( getContentTypes ).first();
        Preconditions.checkArgument( contentType != null, "ContentType [%s] not found", qualifiedContentTypeName );
        return contentType;
    }


    private ContentId contentIdOrNull( final String value )
    {
        if ( value == null )
        {
            return null;
        }
        return ContentId.from( value );
    }

    private ContentPath contentPathOrNull( final String value )
    {
        if ( value == null )
        {
            return null;
        }
        return ContentPath.from( value );
    }
}
