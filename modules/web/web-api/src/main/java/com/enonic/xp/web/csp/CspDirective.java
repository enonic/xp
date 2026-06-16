package com.enonic.xp.web.csp;

/**
 * The known Content Security Policy directive names, per W3C CSP Level 3, grouped by category. Pass
 * these constants to the {@link ContentSecurityPolicy#add}, {@link ContentSecurityPolicy#override},
 * {@link ContentSecurityPolicy#reset} and {@link ContentSecurityPolicy#directive} methods — which take
 * directive names as {@code String} — to avoid bare string literals. The per-directive helper methods
 * ({@link ContentSecurityPolicy#scriptSrc} and friends) cover the same fetch/navigation/document
 * directives with typed {@link CspSource} sources. Directives outside this set (vendor-specific or
 * newer than this class) are still reachable by passing their name as a {@code String}.
 */
public final class CspDirective
{
    // Fetch directives -- govern where resources of a given type may be loaded from.
    public static final String DEFAULT_SRC = "default-src";

    public static final String SCRIPT_SRC = "script-src";

    public static final String SCRIPT_SRC_ELEM = "script-src-elem";

    public static final String SCRIPT_SRC_ATTR = "script-src-attr";

    public static final String STYLE_SRC = "style-src";

    public static final String STYLE_SRC_ELEM = "style-src-elem";

    public static final String STYLE_SRC_ATTR = "style-src-attr";

    public static final String IMG_SRC = "img-src";

    public static final String FONT_SRC = "font-src";

    public static final String CONNECT_SRC = "connect-src";

    public static final String MEDIA_SRC = "media-src";

    public static final String OBJECT_SRC = "object-src";

    public static final String FRAME_SRC = "frame-src";

    public static final String WORKER_SRC = "worker-src";

    public static final String MANIFEST_SRC = "manifest-src";

    public static final String CHILD_SRC = "child-src";

    // Document directives -- govern the document/worker environment a policy applies to.
    public static final String BASE_URI = "base-uri";

    public static final String SANDBOX = "sandbox";

    // Navigation directives -- govern where the document may navigate or submit to.
    public static final String FORM_ACTION = "form-action";

    public static final String FRAME_ANCESTORS = "frame-ancestors";

    // Reporting directive -- names the endpoint group violation reports are sent to.
    public static final String REPORT_TO = "report-to";

    // Other directives.
    public static final String REQUIRE_TRUSTED_TYPES_FOR = "require-trusted-types-for";

    public static final String TRUSTED_TYPES = "trusted-types";

    public static final String UPGRADE_INSECURE_REQUESTS = "upgrade-insecure-requests";

    private CspDirective()
    {
    }
}
