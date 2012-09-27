package com.enonic.wem.web.rest.rpc.content;


import org.codehaus.jackson.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.web.json.rpc.JsonRpcContext;
import com.enonic.wem.web.rest.rpc.AbstractDataRpcHandler;
import com.enonic.wem.web.rest.service.upload.UploadService;

@Component
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
        final ObjectNode data = context.param( "content" ).asObject();
        System.out.println( data.toString() );

        context.setResult( CreateOrUpdateContentJsonResult.created() );
    }

    @Autowired
    public void setUploadService( final UploadService uploadService )
    {
        this.uploadService = uploadService;
    }
}
