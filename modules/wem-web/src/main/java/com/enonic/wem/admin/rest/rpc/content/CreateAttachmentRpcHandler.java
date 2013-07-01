package com.enonic.wem.admin.rest.rpc.content;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import com.enonic.wem.admin.json.JsonErrorResult;
import com.enonic.wem.admin.jsonrpc.JsonRpcContext;
import com.enonic.wem.admin.jsonrpc.JsonRpcError;
import com.enonic.wem.admin.jsonrpc.JsonRpcException;
import com.enonic.wem.admin.rest.rpc.AbstractDataRpcHandler;
import com.enonic.wem.admin.rest.service.upload.UploadItem;
import com.enonic.wem.admin.rest.service.upload.UploadService;
import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentSelector;
import com.enonic.wem.api.content.attachment.Attachment;
import com.enonic.wem.api.content.binary.Binary;

import static com.enonic.wem.api.content.attachment.Attachment.newAttachment;


public final class CreateAttachmentRpcHandler
    extends AbstractDataRpcHandler
{
    private UploadService uploadService;

    public CreateAttachmentRpcHandler()
    {
        super( "attachment_create" );
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final String contentId = context.param( "contentId" ).asString();
        final String contentPath = context.param( "contentPath" ).asString();
        final String attachmentName = context.param( "attachmentName" ).asString();
        if ( StringUtils.isBlank( contentId ) && StringUtils.isBlank( contentPath ) )
        {
            throw new JsonRpcException( JsonRpcError.invalidParams( "Parameter [contentId] or [contentPath] must be specified" ) );
        }
        final ContentSelector contentSelector;
        if ( StringUtils.isBlank( contentId ) )
        {
            contentSelector = ContentPath.from( contentPath );
        }
        else
        {
            contentSelector = ContentId.from( contentId );
        }

        final String[] uploadFileIds = context.param( "uploadFileId" ).required().asStringArray();
        final List<String> missingFiles = Lists.newArrayList();
        final List<Attachment> attachmentsCreated = Lists.newArrayList();
        for ( final String uploadFileId : uploadFileIds )
        {
            boolean created = createAttachment( context, contentSelector, uploadFileId, attachmentName, attachmentsCreated );
            if ( !created )
            {
                missingFiles.add( uploadFileId );
            }
        }

        if ( missingFiles.isEmpty() )
        {
            context.setResult( new CreateAttachmentJsonResult( contentSelector, attachmentsCreated ) );
        }
        else
        {
            context.setResult( new JsonErrorResult( "Could not find uploaded files with id: {0}", Joiner.on( "," ).join( missingFiles ) ) );
        }
    }

    private boolean createAttachment( final JsonRpcContext context, final ContentSelector contentSelector, final String uploadFileId,
                                      final String attachmentName, final List<Attachment> attachmentsCreated )
        throws FileNotFoundException
    {
        final UploadItem uploadItem = uploadService.getItem( uploadFileId );
        if ( uploadItem == null )
        {
            context.setResult( new JsonErrorResult( "Upload file not found" ) );
            return false;
        }

        final Binary binary = Binary.from( new FileInputStream( uploadItem.getFile() ) );
        final Attachment attachment = newAttachment().
            name( attachmentName != null ? attachmentName : uploadItem.getName() ).
            mimeType( uploadItem.getMimeType() ).
            binary( binary ).
            build();

        client.execute( Commands.attachment().create().contentSelector( contentSelector ).attachment( attachment ) );
        attachmentsCreated.add( attachment );
        return true;
    }

    @Inject
    public void setUploadService( final UploadService uploadService )
    {
        this.uploadService = uploadService;
    }
}
