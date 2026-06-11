package com.enonic.xp.web.csp;

/**
 * Flags allowed in a CSP {@code sandbox} directive, per W3C CSP3. Sandbox tokens are emitted
 * unquoted.
 */
public enum SandboxFlag
{
    ALLOW_SCRIPTS( "allow-scripts" ),
    ALLOW_SAME_ORIGIN( "allow-same-origin" ),
    ALLOW_FORMS( "allow-forms" ),
    ALLOW_POPUPS( "allow-popups" ),
    ALLOW_MODALS( "allow-modals" ),
    ALLOW_TOP_NAVIGATION( "allow-top-navigation" ),
    ALLOW_DOWNLOADS( "allow-downloads" ),
    ALLOW_POINTER_LOCK( "allow-pointer-lock" ),
    ALLOW_PRESENTATION( "allow-presentation" ),
    ALLOW_ORIENTATION_LOCK( "allow-orientation-lock" );

    private final String token;

    SandboxFlag( final String token )
    {
        this.token = token;
    }

    /**
     * The unquoted CSP sandbox token (e.g. {@code allow-scripts}).
     */
    public String token()
    {
        return this.token;
    }
}
