package com.enonic.wem.web.rest.resource.account;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.account.AccountKeys;
import com.enonic.wem.api.account.AccountType;
import com.enonic.wem.api.account.Accounts;
import com.enonic.wem.api.account.query.AccountQuery;
import com.enonic.wem.api.account.query.AccountQueryHits;
import com.enonic.wem.api.command.Commands;


@Path("account/export")
public class AccountExportResource
{
    private final DateTimeFormatter filenameFormatter;

    private final AccountCsvExport csvExporter;

    private Client client;

    public AccountExportResource()
    {
        csvExporter = new AccountCsvExport();
        filenameFormatter = DateTimeFormat.forPattern( "yyyyMMdd'T'HHmmss" );
    }

    @GET
    @Path("query")
    public Response byQuery( @QueryParam("query") @DefaultValue("") String query,
                             @QueryParam("type") @DefaultValue("user,group,role") String types,
                             @QueryParam("userStores") @DefaultValue("") String userStores,
                             @QueryParam("encoding") @DefaultValue("ISO-8859-1") String characterEncoding,
                             @QueryParam("separator") @DefaultValue("\t") String separator )
        throws UnsupportedEncodingException
    {
        final AccountQuery accountQueryCount = new AccountQuery( query ).userStores( userStores ).types( parseTypes( types ) ).limit( 0 );
        final int accountsTotal = this.client.execute( Commands.account().find().query( accountQueryCount ) ).getTotalSize();

        final AccountQuery accountQuery =
            new AccountQuery( query ).userStores( userStores ).types( parseTypes( types ) ).limit( accountsTotal );
        final AccountQueryHits accountQueryHits = this.client.execute( Commands.account().find().query( accountQuery ) );
        final AccountKeys accountKeys = accountQueryHits.getKeys();

        return exportAccounts( accountKeys, characterEncoding, separator );
    }

    @POST
    @Path("keys")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response byKeys( @FormParam("key") List<String> keys,
                            @FormParam("encoding") @DefaultValue("ISO-8859-1") String characterEncoding,
                            @FormParam("separator") @DefaultValue("\t") String separator )
        throws UnsupportedEncodingException
    {
        final AccountKeys accountKeys = AccountKeys.from( Iterables.toArray( keys, String.class ) );
        return exportAccounts( accountKeys, characterEncoding, separator );
    }

    private Response exportAccounts( final AccountKeys accountKeys, final String characterEncoding, final String separator )
        throws UnsupportedEncodingException
    {
        final Accounts accounts = this.client.execute( Commands.account().get().keys( accountKeys ).includeProfile() );
        final String csvContent = csvExporter.generateCsvContent( accounts, separator );

        final String attachmentHeader = "attachment; filename=" + getExportFileName( new Date() );
        return Response.ok( csvContent.getBytes( characterEncoding ) ).type( "text/csv" ).
            header( "Content-Encoding", characterEncoding ).
            header( "Content-Disposition", attachmentHeader ).build();
    }

    private AccountType[] parseTypes( final String types )
    {
        if ( types.trim().isEmpty() )
        {
            return new AccountType[]{AccountType.GROUP, AccountType.ROLE, AccountType.USER};
        }
        final Set<String> accountTypeSet = Sets.newHashSet( types.split( "," ) );
        return Iterables.toArray( Collections2.transform( accountTypeSet, new ParseAccountTypeFunction() ), AccountType.class );
    }

    private final static class ParseAccountTypeFunction
        implements Function<String, AccountType>
    {
        @Override
        public AccountType apply( final String value )
        {
            return AccountType.valueOf( value.trim().toUpperCase() );
        }
    }

    private String getExportFileName( Date timestamp )
    {
        final String dateFormatted = filenameFormatter.print( new DateTime( timestamp ) );
        return String.format( "Accounts-%s.csv", dateFormatted );
    }

    @Inject
    public final void setClient( final Client client )
    {
        this.client = client;
    }
}
