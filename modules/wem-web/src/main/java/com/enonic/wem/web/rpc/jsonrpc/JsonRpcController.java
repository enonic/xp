package com.enonic.wem.web.rpc.jsonrpc;

import javax.ws.rs.Path;

import org.springframework.stereotype.Component;

import com.enonic.wem.web.rpc.controller.WebRpcController;

@Component
@Path("rpc/json")
public final class JsonRpcController
    extends WebRpcController
{
    public JsonRpcController()
    {
        super( new JsonRpcMessageHelper() );
    }
}
