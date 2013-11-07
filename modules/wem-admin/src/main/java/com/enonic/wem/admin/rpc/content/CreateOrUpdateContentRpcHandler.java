package com.enonic.wem.admin.rpc.content;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import com.enonic.wem.admin.json.JsonErrorResult;
import com.enonic.wem.admin.jsonrpc.JsonRpcContext;
import com.enonic.wem.admin.jsonrpc.JsonRpcError;
import com.enonic.wem.admin.jsonrpc.JsonRpcException;
import com.enonic.wem.admin.rest.service.upload.UploadItem;
import com.enonic.wem.admin.rest.service.upload.UploadService;
import com.enonic.wem.admin.rpc.AbstractDataRpcHandler;
import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.CreateContent;
import com.enonic.wem.api.command.content.CreateContentResult;
import com.enonic.wem.api.command.content.UpdateContent;
import com.enonic.wem.api.command.content.UpdateContentResult;
import com.enonic.wem.api.command.schema.content.GetContentTypes;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.CreateContentException;
import com.enonic.wem.api.content.RenameContentException;
import com.enonic.wem.api.content.UpdateContentException;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.content.binary.Binary;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;

import static com.enonic.wem.api.command.Commands.content;
import static com.enonic.wem.api.content.attachment.Attachment.newAttachment;
import static com.enonic.wem.api.content.editor.ContentEditors.composite;
import static com.enonic.wem.api.content.editor.ContentEditors.setContentData;
import static com.enonic.wem.api.content.editor.ContentEditors.setContentDisplayName;


public final class CreateOrUpdateContentRpcHandler
    extends AbstractDataRpcHandler
{

    private UploadService uploadService;

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
            throws FileNotFoundException
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

                final ContentTypeName qualifiedContentTypeName =
                    ContentTypeName.from( context.param( "qualifiedContentTypeName" ).required().asString() );
                final ContentType contentType = getContentType( qualifiedContentTypeName );
                final ContentData contentData =
                    new ContentDataParser( contentType ).parse( context.param( "contentData" ).required().asObject() );
                final String displayName = context.param( "displayName" ).notBlank().asString();
                final Collection<Attachment> attachments = getAttachments( context );

                final CreateContent createContent = content().create().
                    parentContentPath( parentContentPath ).
                    name( contentName ).
                    contentType( qualifiedContentTypeName ).
                    contentData( contentData ).
                    displayName( displayName ).
                    owner( AccountKey.anonymous() ).
                    temporary( temporary ).
                    attachments( attachments );
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
            throws FileNotFoundException
        {
            try
            {
                final String displayName = context.param( "displayName" ).notBlank().asString();
                final ContentTypeName qualifiedContentTypeName =
                    ContentTypeName.from( context.param( "qualifiedContentTypeName" ).required().asString() );
                final ContentType contentType = getContentType( qualifiedContentTypeName );
                final ContentData contentData =
                    new ContentDataParser( contentType ).parse( context.param( "contentData" ).required().asObject() );
                final Collection<Attachment> attachments = getAttachments( context );

                final UpdateContent updateContent = content().update().
                    selector( contentId ).
                    editor( composite( setContentData( contentData ), setContentDisplayName( displayName ) ) ).
                    modifier( AccountKey.anonymous() ).
                    attachments( attachments );

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

    private ContentType getContentType( final ContentTypeName qualifiedContentTypeName )
    {
        final GetContentTypes getContentTypes =
            Commands.contentType().get(). byNames().contentTypeNames( ContentTypeNames.from( qualifiedContentTypeName ) );
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

    private Collection<Attachment> getAttachments( final JsonRpcContext context )
        throws FileNotFoundException, JsonRpcException
    {
        final List<Attachment> attachments = Lists.newArrayList();
        if ( context.hasParam( "attachments" ) )
        {
            final ObjectNode[] attachmentsJson = context.param( "attachments" ).asObjectArray();
            for ( ObjectNode attachmentJson : attachmentsJson )
            {
                final String uploadId = attachmentJson.get( "uploadId" ).asText();
                final String attachmentName = attachmentJson.get( "attachmentName" ).asText();
                attachments.add( createAttachment( uploadId, attachmentName ) );
            }
        }
        return attachments;
    }

    private Attachment createAttachment( final String uploadFileId, final String attachmentName )
        throws FileNotFoundException, JsonRpcException
    {
        final UploadItem uploadItem = uploadService.getItem( uploadFileId );
        if ( uploadItem == null )
        {
            throw new JsonRpcException( JsonRpcError.invalidRequest( "Uploaded file not found: [" + uploadFileId + "]" ) );
        }

        final Binary binary = Binary.from( new FileInputStream( uploadItem.getFile() ) );
        final Attachment attachment = newAttachment().
            name( attachmentName ).
            mimeType( uploadItem.getMimeType() ).
            binary( binary ).
            build();

        return attachment;
    }

    @Inject
    public void setUploadService( final UploadService uploadService )
    {
        this.uploadService = uploadService;
    }

}
