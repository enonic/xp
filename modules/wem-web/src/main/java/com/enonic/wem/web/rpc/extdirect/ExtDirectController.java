package com.enonic.wem.web.rpc.extdirect;

import javax.ws.rs.Path;

import org.springframework.stereotype.Component;

import com.enonic.wem.web.rpc.controller.WebRpcController;

@Component
@Path("rpc/extdirect")
public final class ExtDirectController
    extends WebRpcController
{
    public ExtDirectController()
    {
        super( new ExtDirectMessageHelper() );
    }
}
