package com.enonic.xp.portal.impl.controller;

import org.junit.jupiter.api.Test;

import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.csp.ContentSecurityPolicy;
import com.enonic.xp.web.csp.ContentSecurityPolicySerializer;

import static org.assertj.core.api.Assertions.assertThat;

class ControllerScriptImpl_cspTest
    extends AbstractControllerTest
{
    @Test
    void directHeadersAreFoldedIntoTheRequestPolicy()
    {
        this.portalRequest.setMethod( HttpMethod.GET );
        this.portalRequest.getContentSecurityPolicy().add( "img-src", "data:" );

        execute( "myapplication:/controller/cspheader.js" );

        // the directly-set headers replaced the policies and do
        // not travel as plain headers; the platform serializes the composed value at the end
        final ContentSecurityPolicy policy = this.portalRequest.getContentSecurityPolicy();
        assertThat( ContentSecurityPolicySerializer.serialize( policy ) ).isEqualTo( "default-src 'none'; script-src 'self'" );
        assertThat( ContentSecurityPolicySerializer.serialize( policy.reportOnly() ) ).isEqualTo( "script-src 'none'" );
        assertThat( this.portalResponse.getHeaders() ).doesNotContainKeys( "Content-Security-Policy",
                                                                           "content-security-policy-report-only" )
            .containsEntry( "X-Custom", "kept" );
    }

    @Test
    void contributionsAfterTheFoldApplyOnTop()
    {
        this.portalRequest.setMethod( HttpMethod.GET );

        execute( "myapplication:/controller/cspheader.js" );

        this.portalRequest.getContentSecurityPolicy().add( "script-src", "https://cdn.example.com" );
        assertThat( ContentSecurityPolicySerializer.serialize( this.portalRequest.getContentSecurityPolicy() ) ).isEqualTo(
            "default-src 'none'; script-src 'self' https://cdn.example.com" );
    }
}
