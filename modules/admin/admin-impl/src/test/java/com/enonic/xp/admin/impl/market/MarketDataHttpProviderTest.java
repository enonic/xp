package com.enonic.xp.admin.impl.market;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Protocol;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import com.enonic.xp.market.MarketException;

import static org.junit.jupiter.api.Assertions.*;

public class MarketDataHttpProviderTest
{
    private MarketDataHttpProvider provider;

    private String marketUrl = "https://market.enonic.com/applications";

    @BeforeEach
    public void setUp()
        throws Exception
    {
        this.provider = new MarketDataHttpProvider();
    }

    @Test
    public void test_403()
        throws Exception
    {
        testStatus( 403 );
    }

    @Test
    public void test_404()
        throws Exception
    {
        testStatus( 404 );
    }

    @Test
    public void test_500()
        throws Exception
    {
        testStatus( 500 );
    }

    private void testStatus( final int code )
    {
        final Response response = createResponse( code );

        try
        {
            provider.parseResponse( response );
        }
        catch ( MarketException e )
        {
            e.printStackTrace();
            assertEquals( code, e.getHttpErrorCode() );
        }
    }

    private Response createResponse( final int code )
    {
        return new Response.Builder().
            code( code ).
            request( new Request.Builder().
                url( this.marketUrl ).
                build() ).
            protocol( Protocol.HTTP_1_1 ).
            body( ResponseBody.create( MediaType.parse( "application/json" ), "this is my body" ) ).
            build();
    }
}
