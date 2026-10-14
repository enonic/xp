package com.enonic.xp.portal.impl.idprovider;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jspecify.annotations.Nullable;

import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.User;

/**
 * What a verification page has to show. XP's default page renders it, and an id provider's
 * {@code deviceVerification(req)} function receives it as {@code req.deviceLogin}, as plain data:
 * <pre>
 * {
 *   idProvider: "system",
 *   action: "enter" | "confirm" | "done" | "wrongAccount",
 *   user: { key, login, displayName },
 *   error: "...",                          // enter: why the previous code was refused, if any
 *   userCode: "ABCD-EFGH",                 // confirm
 *   clientId: "my-cli",                    // confirm
 *   scope: [ { id, description, principals: [...] } ],   // confirm: the scopes asked for
 *   audience: [ "api" ],                   // confirm
 *   csrf: "...",                           // confirm: the form must post it back as _csrf
 *   approved: true | false                 // done
 * }
 * </pre>
 * The confirm form posts {@code user_code}, {@code approve} ({@code true} or {@code false}),
 * {@code _csrf} and, repeated, {@code scope} for every scope the user keeps; a scope left out is
 * not granted.
 */
sealed interface DeviceLoginContext
    permits DeviceLoginContext.Enter, DeviceLoginContext.Confirm, DeviceLoginContext.Done, DeviceLoginContext.WrongAccount
{
    IdProviderKey idProvider();

    User user();

    /**
     * Enter the code the device shows; {@code error} says why the previous one was refused.
     */
    record Enter(IdProviderKey idProvider, User user, @Nullable String error)
        implements DeviceLoginContext
    {
    }

    /**
     * Approve or deny a pending request, keeping or dropping each requested scope.
     */
    record Confirm(IdProviderKey idProvider, User user, String userCode, @Nullable String clientId, List<Scope> scope,
                   List<String> audience, String csrf)
        implements DeviceLoginContext
    {
        public Confirm
        {
            scope = List.copyOf( scope );
            audience = List.copyOf( audience );
        }

        static Confirm of( final IdProviderKey idProvider, final User user, final String userCode, final @Nullable String clientId,
                           final Set<String> scope, final Set<String> audience, final String csrf )
        {
            final List<Scope> scopes = scope.stream().map( id -> new Scope( id, id, List.of() ) ).toList();
            return new Confirm( idProvider, user, userCode, clientId, scopes, List.copyOf( audience ), csrf );
        }
    }

    /**
     * A requested scope as shown on the page.
     */
    record Scope(String id, String description, List<String> principals)
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
            case Confirm( _, _, var userCode, var clientId, var scope, var audience, var csrf ) ->
            {
                map.put( "action", "confirm" );
                map.put( "userCode", userCode );
                map.put( "clientId", clientId );
                map.put( "scope", scope.stream().<Map<String, Object>>map( s -> {
                    final Map<String, Object> entry = new LinkedHashMap<>();
                    entry.put( "id", s.id() );
                    entry.put( "description", s.description() );
                    entry.put( "principals", s.principals() );
                    return entry;
                } ).toList() );
                map.put( "audience", audience );
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
