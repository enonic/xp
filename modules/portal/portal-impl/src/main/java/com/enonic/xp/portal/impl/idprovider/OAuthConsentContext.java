package com.enonic.xp.portal.impl.idprovider;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jspecify.annotations.Nullable;

import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.User;
import com.enonic.xp.security.token.Scope;
import com.enonic.xp.security.token.Scopes;

/**
 * What the consent page shows. The id provider's {@code oauthConsent(req)} function
 * receives it as {@code req.oauthConsent}, as plain data:
 * <pre>
 * {
 *   idProvider: "system",
 *   action: "enter" | "confirm" | "done" | "wrongAccount",
 *   user: { key, login, displayName },
 *   error: "invalid_code",                 // enter: why the previous code was refused, if any; the page words it
 *   userCode: "BCDF-GHJK",                 // confirm
 *   clientId: "my-cli",                    // confirm
 *   scope: [ { id, title, description, roles: [...], required } ],
 *                                          // confirm: the scopes asked for, each as the id provider
 *                                          //          defines it, and whether the user may drop it
 *   unrestricted: true | false,            // confirm: the id provider has no scopes, the token gets the user's full rights
 *   audience: [ "api" ],                   // confirm
 *   csrf: "...",                           // confirm: the form must post it back as _csrf
 *   approved: true | false                 // done
 * }
 * </pre>
 * The confirm form posts {@code user_code}, {@code approve} ({@code true} or {@code false}),
 * {@code _csrf} and one {@code scope} per scope the user keeps. A scope left out is not granted,
 * except a required one, which is granted whenever the request is approved. The page may word a
 * scope itself or show the title and description its id provider gave it.
 */
sealed interface OAuthConsentContext
    permits OAuthConsentContext.Enter, OAuthConsentContext.Confirm, OAuthConsentContext.Done, OAuthConsentContext.WrongAccount
{
    /**
     * The code entered was unknown, used or expired. Given as a code, not a sentence, so the page can
     * word it in the user's language.
     */
    String INVALID_CODE = "invalid_code";

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
     * what the client called itself; nothing verifies it.
     */
    record Confirm(IdProviderKey idProvider, User user, String userCode, String clientId, List<RequestedScope> scope,
                   List<String> audience, boolean unrestricted, String csrf)
        implements OAuthConsentContext
    {
        public Confirm
        {
            scope = List.copyOf( scope );
            audience = List.copyOf( audience );
        }

        /**
         * @param known the id provider's scopes
         */
        static Confirm of( final IdProviderKey idProvider, final User user, final String userCode, final String clientId,
                           final Set<String> scope, final Set<String> audience, final Scopes known, final String csrf )
        {
            final List<RequestedScope> scopes = scope.stream().map( id -> {
                final Scope found = known.get( id ).orElse( null );
                return found == null
                    ? new RequestedScope( id, id, null, List.of(), false )
                    : new RequestedScope( id, found.getTitle(), found.getDescription(),
                                          found.getRoles().stream().map( PrincipalKey::toString ).toList(), found.isRequired() );
            } ).toList();
            return new Confirm( idProvider, user, userCode, clientId, scopes, List.copyOf( audience ), known.isEmpty(), csrf );
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
            case Confirm( _, _, var userCode, var clientId, var scope, var audience, var unrestricted, var csrf ) ->
            {
                map.put( "action", "confirm" );
                map.put( "userCode", userCode );
                map.put( "clientId", clientId );
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
            }
            case Done( _, _, var approved ) ->
            {
                map.put( "action", "done" );
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
