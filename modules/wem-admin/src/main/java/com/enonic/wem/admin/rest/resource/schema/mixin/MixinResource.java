package com.enonic.wem.admin.rest.resource.schema.mixin;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.enonic.wem.admin.json.schema.mixin.MixinJson;
import com.enonic.wem.admin.json.schema.mixin.MixinListJson;
import com.enonic.wem.admin.rest.resource.schema.SchemaIconResolver;
import com.enonic.wem.admin.rest.resource.schema.SchemaIconUrlResolver;
import com.enonic.wem.api.schema.mixin.GetMixinParams;
import com.enonic.wem.api.schema.mixin.Mixin;
import com.enonic.wem.api.schema.mixin.MixinName;
import com.enonic.wem.api.schema.mixin.MixinService;
import com.enonic.wem.api.schema.mixin.Mixins;

@Path("schema/mixin")
@Produces(MediaType.APPLICATION_JSON)
public class MixinResource
{
    private MixinService mixinService;

    @GET
    public MixinJson get( @QueryParam("name") final String name )
    {
        final MixinName mixinName = MixinName.from( name );
        final Mixin mixin = fetchMixin( mixinName );

        if ( mixin == null )
        {
            String message = String.format( "Mixin [%s] was not found.", mixinName );
            throw new WebApplicationException( Response.status( Response.Status.NOT_FOUND ).
                entity( message ).type( MediaType.TEXT_PLAIN_TYPE ).build() );
        }

        return new MixinJson( mixin, newSchemaIconUrlResolver() );
    }

    @GET
    @Path("list")
    public MixinListJson list()
    {
        final Mixins mixins = mixinService.getAll();

        return new MixinListJson( mixins, new SchemaIconUrlResolver( new SchemaIconResolver( mixinService ) ) );
    }

    private Mixin fetchMixin( final MixinName name )
    {
        return mixinService.getByName( new GetMixinParams( name ) );
    }

    private SchemaIconUrlResolver newSchemaIconUrlResolver()
    {
        return new SchemaIconUrlResolver( new SchemaIconResolver( mixinService ) );
    }

    public void setMixinService( final MixinService mixinService )
    {
        this.mixinService = mixinService;
    }
}
