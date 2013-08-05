package com.enonic.wem.admin.rest.resource.schema.mixin;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.enonic.wem.admin.rest.resource.AbstractResource;
import com.enonic.wem.admin.rest.resource.schema.mixin.model.AbstractMixinJson;
import com.enonic.wem.admin.rest.resource.schema.mixin.model.MixinConfigJson;
import com.enonic.wem.admin.rest.resource.schema.mixin.model.MixinGetJson;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.Mixins;
import com.enonic.wem.api.schema.mixin.QualifiedMixinName;
import com.enonic.wem.api.schema.mixin.QualifiedMixinNames;

import static com.enonic.wem.api.command.Commands.mixin;

@Path("schema/mixin")
@Produces(MediaType.APPLICATION_JSON)
public class MixinResource extends AbstractResource
{
    public static final String FORMAT_XML = "XML";

    public static final String FORMAT_JSON = "JSON";

    @GET
    public AbstractMixinJson get(@QueryParam("mixin") final String name,
                                 @QueryParam("format") final String format)
    {
        final QualifiedMixinName qualifiedMixinName = new QualifiedMixinName(name);
        final Mixin mixin = fetchMixin(qualifiedMixinName);

        if ( mixin == null )
        {
            String message = String.format("Mixin [%s] was not found.", qualifiedMixinName);
            throw new WebApplicationException( Response.serverError().entity( message ).type( MediaType.TEXT_PLAIN_TYPE ).build() );
        }

        if ( FORMAT_JSON.equalsIgnoreCase( format ))
        {
            return new MixinGetJson( mixin );
        }
        else if ( FORMAT_XML.equalsIgnoreCase( format ))
        {
            return new MixinConfigJson( mixin );
        }
        else
        {
            String message = String.format("Response format [%s] doesn't exist.", format);
            throw new WebApplicationException( Response.serverError().entity( message ).type( MediaType.TEXT_PLAIN_TYPE ).build() );
        }
    }

    private Mixin fetchMixin( final QualifiedMixinName qualifiedName )
    {
        final QualifiedMixinNames qualifiedNames = QualifiedMixinNames.from( qualifiedName );
        final Mixins mixins = client.execute( mixin().get().names( qualifiedNames ) );
        return mixins.isEmpty() ? null : mixins.first();
    }
}
