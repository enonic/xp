package com.enonic.wem.web.data.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enonic.wem.web.data.AbstractDataRpcHandler;
import com.enonic.wem.web.json.JsonSerializable;
import com.enonic.wem.web.rest2.resource.locale.LocaleResource;
import com.enonic.wem.web.jsonrpc.JsonRpcContext;

@Component
public final class GetLocalesRpcHandler
    extends AbstractDataRpcHandler
{
    @Autowired
    private LocaleResource resource;

    public GetLocalesRpcHandler()
    {
        super( "util_getLocales" );
    }

    @Override
    public void handle( final JsonRpcContext context )
        throws Exception
    {
        final JsonSerializable json = this.resource.getAll();
        context.setResult( json );
    }
}
