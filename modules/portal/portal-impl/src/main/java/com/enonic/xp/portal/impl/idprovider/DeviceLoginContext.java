package com.enonic.xp.portal.impl.idprovider;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jspecify.annotations.Nullable;

import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.web.vhost.VirtualHostIdProvider;

/**
 * What a verification page has to show. XP's default page renders it, and an id provider's
 * {@code deviceVerification(req)} function receives it as {@code req.deviceLogin}, as plain data:
 * <pre>
 * {
 *   idProvider: "system",
 *   action: "enter" | "confirm" | "done" | "wrongAccount",
 *   user: { key, login, displayName },
 *   error: "invalid_code",                 // enter: why the previous code was refused, if any; a code, the page words it
 *   userCode: "ABCD-EFGH",                 // confirm
 *   clientId: "my-cli",                    // confirm
 *   scope: [ { id, roles: [...], required } ], // confirm: the scopes asked for, the roles each implies,
 *                                          //          and whether the user may drop it
 *   unrestricted: true | false,            // confirm: no catalogue here, the token gets the user's full rights
 *   audience: [ "api" ],                   // confirm
 *   csrf: "...",                           // confirm: the form must post it back as _csrf
 *   approved: true | false                 // done
 * }
 * </pre>
 * The confirm form posts {@code user_code}, {@code approve} ({@code true} or {@code false}),
 * {@code _csrf} and, repeated, {@code scope} for every scope the user keeps; a scope left out is
 * not granted, except a required one, which is granted whenever the request is approved. The page
 * owns the wording: what a scope id means to a user is the page's to say,
 * typically from its own phrases, falling back to the id.
 */
sealed interface DeviceLoginContext
    permits DeviceLoginContext.Enter, DeviceLoginContext.Confirm, DeviceLoginContext.Done, DeviceLoginContext.WrongAccount
{
    /**
     * The code entered was unknown, already used, or expired. The only error the enter page is told; as a code, not a
     * sentence, so the page can word it in the user's language.
     */
    String INVALID_CODE = "invalid_code";

    IdProviderKey idProvider();

    User user();

    /**
     * Enter the code the device shows; {@code error} says why the previous one was refused, as an error code.
     */
    record Enter(IdProviderKey idProvider, User user, @Nullable String error)
        implements DeviceLoginContext
    {
    }

    /**
     * Approve or deny a pending request, keeping or dropping each requested scope.
     */
    record Confirm(IdProviderKey idProvider, User user, String userCode, @Nullable String clientId, List<Scope> scope,
                   List<String> audience, boolean unrestricted, String csrf)
        implements DeviceLoginContext
    {
        public Confirm
        {
            scope = List.copyOf( scope );
            audience = List.copyOf( audience );
        }

        /**
         * @param settings the id provider's settings on the vhost: the scope catalogue and the required scopes
         */
        static Confirm of( final IdProviderKey idProvider, final User user, final String userCode, final @Nullable String clientId,
                           final Set<String> scope, final Set<String> audience, final VirtualHostIdProvider settings, final String csrf )
        {
            final List<Scope> scopes = scope.stream()
                .map( id -> new Scope( id, settings.getScopes().getOrDefault( id, PrincipalKeys.empty() ).stream().map( PrincipalKey::toString ).toList(),
                                       settings.getRequiredScopes().contains( id ) ) )
                .toList();
            return new Confirm( idProvider, user, userCode, clientId, scopes, List.copyOf( audience ), settings.getScopes().isEmpty(), csrf );
        }
    }

    /**
     * A requested scope as shown on the page, with the roles it implies and whether the user may drop it.
     */
    record Scope(String id, List<String> roles, boolean required)
    {
    }

    /**
     * The decision was recorded.
     */
    record Done(IdProviderKey idProvider, User user, boolean approved)
        implements DeviceLoginContext
    {
    }

    /**
     * The signed-in user belongs to another id provider than the one the request is for.
     */
    record WrongAccount(IdProviderKey idProvider, User user)
        implements DeviceLoginContext
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
