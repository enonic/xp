package com.enonic.xp.web.impl.header;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HeaderFilterWrapperTest
{
    @Mock
    HeaderFilterConfig headerFilterConfig;

    @Mock
    HttpServletResponse httpServletResponse;

    @Test
    void test()
        throws Exception
    {
        when( headerFilterConfig.headerConfig() ).thenReturn( "set X-Content-Type-Options: nosniff" );
        final HeaderFilterWrapper instance = new HeaderFilterWrapper( headerFilterConfig );

        final HttpServletRequest requestMock = mock( HttpServletRequest.class );
        when( requestMock.getServletContext() ).thenReturn( mock( ServletContext.class ) );
        instance.doHandle( requestMock, httpServletResponse, mock( FilterChain.class ) );
        instance.deactivate();

        verify( httpServletResponse ).setHeader( "X-Content-Type-Options", "nosniff" );
    }
}
