package com.enonic.xp.portal.impl.idprovider;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.jspecify.annotations.Nullable;

import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.User;
import com.enonic.xp.security.token.Client;
import com.enonic.xp.security.token.Scope;
import com.enonic.xp.security.token.Scopes;

/**
 * What the consent page shows, for device login and for the authorization code flow. The id
 * provider's {@code oauthConsent(req)} function receives it as {@code req.oauthConsent}, as
 * plain data:
 * <pre>
 * {
 *   idProvider: "system",
 *   action: "enter" | "confirm" | "done" | "wrongAccount",
 *   flow: "device" | "code",               // confirm, done: which flow the request belongs to
 *   user: { key, login, displayName },
 *   error: "invalid_code",                 // enter: why the previous code was refused, if any; the page words it
 *   userCode: "BCDF-GHJK",                 // confirm, device flow
 *   requestId: "...",                      // confirm, code flow: the form must post it back as request
 *   clientId: "my-cli",                    // confirm
 *   client: { id, displayName, uri, logoUri, policyUri, tosUri, registered },
 *                                          // confirm: the client as XP knows it; registered is true when the
 *                                          //          id provider's administrators registered it, false when
 *                                          //          it registered itself and its name is only its own claim
 *   scope: [ { id, title, description, roles: [...], required } ],
 *                                          // confirm: the scopes asked for, each as the id provider
 *                                          //          defines it, and whether the user may drop it
 *   unrestricted: true | false,            // confirm: the request carries no scope, the token gets the user's full rights
 *   audience: [ "api" ],                   // confirm
 *   csrf: "...",                           // confirm: the form must post it back as _csrf
 *   approved: true | false                 // done
 * }
 * </pre>
 * The confirm form posts {@code user_code} (device flow) or {@code request} (code flow),
 * {@code approve} ({@code true} or {@code false}), {@code _csrf} and one {@code scope} per scope
 * the user keeps. A scope left out is not granted, except a required one, which is granted
 * whenever the request is approved. The page may word a scope itself or show the title and
 * description its id provider gave it. In the device flow the page then shows {@code done}; in the
 * code flow the browser is sent back to the client.
 */
sealed interface OAuthConsentContext
    permits OAuthConsentContext.Enter, OAuthConsentContext.Confirm, OAuthConsentContext.Done, OAuthConsentContext.WrongAccount
{
    /**
     * The code entered was unknown, used or expired. Given as a code, not a sentence, so the page can
     * word it in the user's language.
     */
    String INVALID_CODE = "invalid_code";

    String DEVICE_FLOW = "device";

    String CODE_FLOW = "code";

    IdProviderKey idProvider();

    User user();

    /**
     * Enter the code the device shows; {@code error} is the code of why the previous one was refused.
     */
    record Enter(IdProviderKey idProvider, User user, @Nullable String error)
        implements OAuthConsentContext
    {
    }

    /**
     * Approve or deny a pending request, keeping or dropping each requested scope. The client id is
     * what the client called itself; nothing verifies it. A registered client's record supplies
     * the name and links the page shows.
     */
    record Confirm(IdProviderKey idProvider, User user, String flow, @Nullable String userCode, @Nullable String requestId, String clientId,
                   ClientInfo client, List<RequestedScope> scope, List<String> audience, boolean unrestricted, String csrf,
                   @Nullable String redirectOrigin)
        implements OAuthConsentContext
    {
        public Confirm
        {
            scope = List.copyOf( scope );
            audience = List.copyOf( audience );
        }

        /**
         * A device login's confirmation, by user code.
         *
         * @param client         the client's record
         * @param selfRegistered whether the client registered itself, rather than the id provider's administrators
         * @param known          the id provider's scopes
         */
        static Confirm forDevice( final IdProviderKey idProvider, final User user, final String userCode, final String clientId,
                                  final Client client, final boolean selfRegistered, final Set<String> scope,
                                  final Set<String> audience, final Scopes known, final String csrf )
        {
            return of( idProvider, user, DEVICE_FLOW, userCode, null, clientId, ClientInfo.of( client, selfRegistered ), scope, audience,
                       known, csrf, null );
        }

        /**
         * An authorization code flow's confirmation, by request id.
         *
         * @param redirectUri the redirect URI the request was checked against, where approving sends the browser
         */
        static Confirm forCode( final IdProviderKey idProvider, final User user, final String requestId, final String clientId,
                                final Client client, final boolean selfRegistered, final Set<String> scope,
                                final Set<String> audience, final Scopes known, final String csrf, final String redirectUri )
        {
            return of( idProvider, user, CODE_FLOW, null, requestId, clientId, ClientInfo.of( client, selfRegistered ), scope, audience,
                       known, csrf, redirectOrigin( redirectUri ) );
        }

        /**
         * Where approving sends the browser, as a page's policy names it: the redirect URI's scheme, host and
         * port, or its scheme alone for a URI without a host, such as a native app's private-use scheme. A page
         * keeps its forms to itself and this, since browsers hold the redirect that follows a form post to the
         * form's policy too.
         */
        static @Nullable String redirectOrigin( final String redirectUri )
        {
            final URI uri;
            try
            {
                uri = new URI( redirectUri );
            }
            catch ( URISyntaxException e )
            {
                return null;
            }
            if ( uri.getScheme() == null )
            {
                return null;
            }
            final String scheme = uri.getScheme().toLowerCase( Locale.ROOT );
            if ( uri.getHost() == null )
            {
                return scheme + ":";
            }
            return scheme + "://" + uri.getHost().toLowerCase( Locale.ROOT ) + ( uri.getPort() >= 0 ? ":" + uri.getPort() : "" );
        }

        private static Confirm of( final IdProviderKey idProvider, final User user, final String flow, final @Nullable String userCode,
                                   final @Nullable String requestId, final String clientId, final ClientInfo client,
                                   final Set<String> scope, final Set<String> audience, final Scopes known, final String csrf,
                                   final @Nullable String redirectOrigin )
        {
            final List<RequestedScope> scopes = scope.stream().map( id -> {
                final Scope found = known.get( id ).orElse( null );
                return found == null
                    ? new RequestedScope( id, id, null, List.of(), false )
                    : new RequestedScope( id, found.getTitle(), found.getDescription(),
                                          found.getRoles().stream().map( PrincipalKey::toString ).toList(), found.isRequired() );
            } ).toList();
            return new Confirm( idProvider, user, flow, userCode, requestId, clientId, client, scopes,
                                List.copyOf( audience ), scope.isEmpty(), csrf, redirectOrigin );
        }
    }

    /**
     * The client as the page may present it: its name and links, and whether the id provider's
     * administrators registered it, rather than the client itself.
     */
    record ClientInfo(String id, String displayName, @Nullable String uri, @Nullable String logoUri, @Nullable String policyUri,
                      @Nullable String tosUri, boolean registered)
    {
        static ClientInfo of( final Client client, final boolean selfRegistered )
        {
            return new ClientInfo( client.getId(), client.getDisplayName(), client.getUri(), client.getLogoUri(), client.getPolicyUri(),
                                   client.getTosUri(), !selfRegistered );
        }
    }

    /**
     * A requested scope as its id provider defines it: its title and description, the roles it
     * implies and whether the user may drop it.
     */
    record RequestedScope(String id, String title, @Nullable String description, List<String> roles, boolean required)
    {
    }

    /**
     * The decision was recorded.
     */
    record Done(IdProviderKey idProvider, User user, boolean approved)
        implements OAuthConsentContext
    {
    }

    /**
     * The signed-in user belongs to another id provider than the one the request is for.
     */
    record WrongAccount(IdProviderKey idProvider, User user)
        implements OAuthConsentContext
    {
    }

    private static void putIfPresent( final Map<String, Object> map, final String key, final @Nullable String value )
    {
        if ( value != null )
        {
            map.put( key, value );
        }
    }

    /**
     * The plain-data form handed to an id provider's page function.
     */
    default Map<String, Object> toMap()
    {
        final Map<String, Object> map = new LinkedHashMap<>();
        map.put( "idProvider", idProvider().toString() );
        switch ( this )
        {
            case Enter( _, _, var error ) ->
            {
                map.put( "action", "enter" );
                if ( error != null )
                {
                    map.put( "error", error );
                }
            }
            case Confirm( _, _, var flow, var userCode, var requestId, var clientId, var client, var scope, var audience, var unrestricted,
                          var csrf, var redirectOrigin ) ->
            {
                map.put( "action", "confirm" );
                map.put( "flow", flow );
                putIfPresent( map, "userCode", userCode );
                putIfPresent( map, "requestId", requestId );
                map.put( "clientId", clientId );
                final Map<String, Object> clientMap = new LinkedHashMap<>();
                clientMap.put( "id", client.id() );
                clientMap.put( "displayName", client.displayName() );
                putIfPresent( clientMap, "uri", client.uri() );
                putIfPresent( clientMap, "logoUri", client.logoUri() );
                putIfPresent( clientMap, "policyUri", client.policyUri() );
                putIfPresent( clientMap, "tosUri", client.tosUri() );
                clientMap.put( "registered", client.registered() );
                map.put( "client", clientMap );
                map.put( "scope", scope.stream().<Map<String, Object>>map( s -> {
                    final Map<String, Object> entry = new LinkedHashMap<>();
                    entry.put( "id", s.id() );
                    entry.put( "title", s.title() );
                    if ( s.description() != null )
                    {
                        entry.put( "description", s.description() );
                    }
                    entry.put( "roles", s.roles() );
                    entry.put( "required", s.required() );
                    return entry;
                } ).toList() );
                map.put( "audience", audience );
                map.put( "unrestricted", unrestricted );
                map.put( "csrf", csrf );
                putIfPresent( map, "redirectOrigin", redirectOrigin );
            }
            case Done( _, _, var approved ) ->
            {
                map.put( "action", "done" );
                map.put( "flow", DEVICE_FLOW );
                map.put( "approved", approved );
            }
            case WrongAccount _ -> map.put( "action", "wrongAccount" );
        }
        final Map<String, Object> user = new LinkedHashMap<>();
        user.put( "key", user().getKey().toString() );
        user.put( "login", user().getLogin() );
        user.put( "displayName", user().getDisplayName() );
        map.put( "user", user );
        return map;
    }
}
