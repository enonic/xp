package com.enonic.wem.web.rpc.extdirect;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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

    @GET
    @Path("api.js")
    @Produces("application/javascript")
    public String getApi( @QueryParam("ns") @DefaultValue("App") final String ns,
                          @QueryParam("action") @DefaultValue("RemoteService") final String action )
    {
        final ObjectNode json = JsonNodeFactory.instance.objectNode();
        json.put( "url", getServiceUrl() );
        json.put( "type", "remoting" );
        json.put( "namespace", ns );

        final ObjectNode actionsJson = json.putObject( "actions" );
        final ArrayNode methodsJson = actionsJson.putArray( action );

        for ( final String methodName : this.getProcessor().getMethodNames() )
        {
            final ObjectNode methodJson = methodsJson.addObject();
            methodJson.put( "name", methodName );
            methodJson.put( "len", 1 );
        }

        final StringBuilder str = new StringBuilder();
        str.append( "Ext.Direct.addProvider(" ).append( json.toString() ).append( ");" );
        return str.toString();
    }

    private String getServiceUrl()
    {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path( "admin/rest/rpc/extdirect" ).build().toString();
    }
}
