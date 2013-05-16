package com.enonic.wem.web.rest.rpc.content;


import java.io.FileInputStream;

import javax.inject.Inject;

import com.enonic.wem.api.content.binary.Binary;
import com.enonic.wem.api.content.binary.BinaryId;
import com.enonic.wem.web.json.JsonErrorResult;
import com.enonic.wem.web.json.rpc.JsonRpcContext;
import com.enonic.wem.web.rest.rpc.AbstractDataRpcHandler;
import com.enonic.wem.web.rest.service.upload.UploadItem;
import com.enonic.wem.web.rest.service.upload.UploadService;

import static com.enonic.wem.api.command.Commands.binary;


public final class CreateBinaryRpcHandler
    extends AbstractDataRpcHandler
{
    private UploadService uploadService;

    public CreateBinaryRpcHandler()
    {
        super( "binary_create" );
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final String uploadFileId = context.param( "uploadFileId" ).required().asString();
        final UploadItem uploadItem = uploadService.getItem( uploadFileId );
        if ( uploadItem == null )
        {
            context.setResult( new JsonErrorResult( "Upload file not found" ) );
            return;
        }

        final Binary binary = Binary.from( new FileInputStream( uploadItem.getFile() ) );
        final BinaryId binaryId = client.execute( binary().create().binary( binary ) );
        context.setResult( new CreateBinaryJsonResult( binaryId ) );
    }

    @Inject
    public void setUploadService( final UploadService uploadService )
    {
        this.uploadService = uploadService;
    }
}
