package com.enonic.xp.portal.impl.controller;

import org.junit.jupiter.api.Test;

import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.csp.ContentSecurityPolicy;

import static org.assertj.core.api.Assertions.assertThat;

class ControllerScriptImpl_cspTest
    extends AbstractControllerTest
{
    @Test
    void directHeadersAreFoldedAsAdditionalPolicies()
    {
        this.portalRequest.setMethod( HttpMethod.GET );
        this.portalRequest.getContentSecurityPolicy().add( "img-src", "data:" );

        execute( "myapplication:/controller/cspheader.js" );

        // the directly-set headers are folded in as additional enforced policies, without replacing
        // the platform's own contributions (img-src survives) and without travelling as plain
        // headers; the platform serializes the composed value at the end
        final ContentSecurityPolicy policy = this.portalRequest.getContentSecurityPolicy();
        assertThat( policy.build() ).isEqualTo( "img-src data:, default-src 'none'; script-src 'self'" );
        assertThat( policy.reportOnly().build() ).isEqualTo( "script-src 'none'" );
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
        assertThat( this.portalRequest.getContentSecurityPolicy().build() ).isEqualTo(
            "script-src https://cdn.example.com, default-src 'none'; script-src 'self'" );
    }
}
