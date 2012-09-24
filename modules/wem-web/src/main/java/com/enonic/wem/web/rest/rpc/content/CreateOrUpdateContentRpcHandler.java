package com.enonic.wem.web.rest.rpc.content;


import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.wem.web.json.rpc.JsonRpcContext;
import com.enonic.wem.web.rest.rpc.AbstractDataRpcHandler;
import com.enonic.wem.web.rest.service.upload.UploadService;

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

    }

    @Autowired
    public void setUploadService( final UploadService uploadService )
    {
        this.uploadService = uploadService;
    }
}
