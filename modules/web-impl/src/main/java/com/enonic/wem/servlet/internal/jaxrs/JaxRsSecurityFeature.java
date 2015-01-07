package com.enonic.wem.servlet.internal.jaxrs;

import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.plugins.interceptors.RoleBasedSecurityFeature;

import com.enonic.xp.web.jaxrs.JaxRsComponent;

@Provider
final class JaxRsSecurityFeature
    extends RoleBasedSecurityFeature
    implements JaxRsComponent
{
}
