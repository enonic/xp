package com.enonic.wem.web.rpc.extdirect;

import javax.ws.rs.Path;

import org.springframework.stereotype.Component;

import com.enonic.wem.web.rpc.controller.WebRpcController;

@Component
@Path("rpc/extdirect")
// TODO: Implement form handler that maps to a standard WebRpcRequest. Form handler will not support upload at this time.
public final class ExtDirectController
    extends WebRpcController
{
    public ExtDirectController()
    {
        super( new ExtDirectMessageHelper() );
    }
}
