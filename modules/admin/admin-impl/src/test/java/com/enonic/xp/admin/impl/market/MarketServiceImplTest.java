package com.enonic.xp.admin.impl.market;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Protocol;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import com.enonic.xp.market.MarketException;

import static org.junit.Assert.*;

public class MarketServiceImplTest
{
    private MarketDataProviderImpl provider;

    private MarketConfig marketConfig;

    private MarketServiceImpl marketService;

    private String marketUrl = "https://market.enonic.com/applications";
    private String marketProxy = "";

    @Before
    public void setUp()
        throws Exception
    {
        marketConfig = Mockito.mock( MarketConfig.class );

        Mockito.when( marketConfig.marketUrl() ).
            thenReturn( this.marketUrl );

        this.provider = Mockito.mock( MarketDataProviderImpl.class );

        this.marketService = new MarketServiceImpl();
        marketService.activate( marketConfig );
        marketService.setProvider( provider );
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
        final String version = "6.3.0";
        final Response response = createResponse( code );

        Mockito.when( provider.fetch( this.marketUrl, this.marketProxy, version, 0, 10 ) ).
            thenReturn( response );

        try
        {
            marketService.get( version, 0, 10 );
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