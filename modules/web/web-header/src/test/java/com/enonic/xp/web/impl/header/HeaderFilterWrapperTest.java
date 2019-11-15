package com.enonic.xp.web.impl.header;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

        instance.init( mock( FilterConfig.class ) );
        instance.doHandle( mock( HttpServletRequest.class ), httpServletResponse, mock( FilterChain.class ) );
        instance.destroy();

        verify( httpServletResponse ).setHeader( "X-Content-Type-Options", "nosniff" );
    }
}
