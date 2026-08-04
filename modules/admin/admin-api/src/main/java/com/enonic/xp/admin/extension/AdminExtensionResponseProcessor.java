package com.enonic.xp.admin.extension;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;

/**
 * Lets an admin extension process the response of admin tool pages the extension is mounted to.
 * <p>
 * Implementations are registered as OSGi services with a mandatory {@code key} service property holding
 * the extension's descriptor key, e.g. {@code key=com.example.app:my-widget}. They run after the tool
 * controller, for tools that share an interface with the extension (or for all tools, if the extension
 * declares the {@code generic} interface), and only when the current user is allowed to access the extension.
 * <p>
 * Typical uses: adjust the tool page's Content-Security-Policy via {@code request.getContentSecurityPolicy()}
 * so the extension can call external APIs from the browser, or add page contributions that load
 * the extension's scripts on the page.
 */
@NullMarked
public interface AdminExtensionResponseProcessor
{
    PortalResponse process( PortalRequest request, PortalResponse response );
}
