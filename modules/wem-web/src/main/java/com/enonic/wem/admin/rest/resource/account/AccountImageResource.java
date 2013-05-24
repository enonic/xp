package com.enonic.wem.admin.rest.resource.account;

import java.awt.image.BufferedImage;

import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.account.Account;
import com.enonic.wem.api.account.AccountKeys;
import com.enonic.wem.api.command.Commands;


@Path("account/image")
@Produces("image/png")
public final class AccountImageResource
{
    private final AccountImageHelper helper;

    private Client client;

    public AccountImageResource()
        throws Exception
    {
        this.helper = new AccountImageHelper();
    }

    @GET
    @Path("{key}")
    public BufferedImage getAccountImage( @PathParam("key") final String key, @QueryParam("size") @DefaultValue("100") final int size )
        throws Exception
    {
        final Account account = this.client.execute( Commands.account().get().keys( AccountKeys.from( key ) ).includeImage() ).first();

        return this.helper.getAccountImage( account, size );
    }

    @GET
    @Path("default/{name}")
    public BufferedImage getDefaultImage( @PathParam("name") final String name, @QueryParam("size") @DefaultValue("100") final int size )
        throws Exception
    {
        return this.helper.getDefaultImage( name, size );
    }

    @Inject
    public void setClient( final Client client )
    {
        this.client = client;
    }
}
