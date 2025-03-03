package com.enonic.xp.admin.impl.app;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import com.google.common.net.HttpHeaders;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class NoCacheAdminFilterTest
{
    private NoCacheAdminFilter noCacheAdminFilter;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @BeforeEach
    void setUp()
    {
        MockitoAnnotations.openMocks( this );
        noCacheAdminFilter = new NoCacheAdminFilter();
    }

    @Test
    void doHandle_setsCacheControlHeader()
        throws Exception
    {
        noCacheAdminFilter.doHandle( request, response, filterChain );

        verify( response ).setHeader( HttpHeaders.CACHE_CONTROL, "private, no-cache" );
        verify( filterChain ).doFilter( eq( request ), any( NoCacheAdminFilter.NoCacheAdminResponseWrapper.class ) );
    }

    @Test
    void NoCacheAdminResponseWrapper_setHeader_replacesPublicWithPrivate()
    {
        HttpServletResponse responseWrapper = new NoCacheAdminFilter.NoCacheAdminResponseWrapper( response );

        responseWrapper.setHeader( HttpHeaders.CACHE_CONTROL, "public, max-age=3600" );

        verify( response ).setHeader( HttpHeaders.CACHE_CONTROL, "private, max-age=3600" );
    }

    @Test
    void NoCacheAdminResponseWrapper_setHeader_alreadyPrivate()
    {
        HttpServletResponse responseWrapper = new NoCacheAdminFilter.NoCacheAdminResponseWrapper( response );

        responseWrapper.setHeader( HttpHeaders.CACHE_CONTROL, "private, max-age=3600" );

        verifyNoInteractions( response );
    }

    @Test
    void NoCacheAdminResponseWrapper_setHeader_addsPrivateIfNotPresent()
    {
        HttpServletResponse responseWrapper = new NoCacheAdminFilter.NoCacheAdminResponseWrapper( response );

        responseWrapper.setHeader( HttpHeaders.CACHE_CONTROL, "max-age=3600" );

        verify( response ).setHeader( HttpHeaders.CACHE_CONTROL, "private, max-age=3600" );
    }

    @Test
    void NoCacheAdminResponseWrapper_setHeader_setsPrivateNoCacheIfValueIsNull()
    {
        HttpServletResponse responseWrapper = new NoCacheAdminFilter.NoCacheAdminResponseWrapper( response );

        responseWrapper.setHeader( HttpHeaders.CACHE_CONTROL, null );

        verify( response ).setHeader( HttpHeaders.CACHE_CONTROL, "private, no-cache" );
    }

    @Test
    void NoCacheAdminResponseWrapper_addHeader_replacesPublicWithPrivate()
    {
        HttpServletResponse responseWrapper = new NoCacheAdminFilter.NoCacheAdminResponseWrapper( response );

        responseWrapper.addHeader( HttpHeaders.CACHE_CONTROL, "public, max-age=3600" );

        verify( response ).addHeader( HttpHeaders.CACHE_CONTROL, "private, max-age=3600" );
    }
}
