package com.enonic.xp.core.impl.security.token;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.token.Client;

/**
 * Self-registered clients (RFC 7591) kept nowhere: the client id is the client's record as a JWS
 * under XP's token signing key, typed {@value #TYP} so it never passes as an access token. It
 * names the id provider and when it runs out; a record that fails any check reads as no client.
 */
@NullMarked
final class StatelessClients
{
    static final String TYP = "client-id+jwt";

    private final TokenSigningKeys keys;

    private final Clock clock;

    private final Duration ttl;

    StatelessClients( final TokenSigningKeys keys, final Clock clock, final Duration ttl )
    {
        this.keys = keys;
        this.clock = clock;
        this.ttl = ttl;
    }

    /**
     * Whether the id has the shape of a signed record, so a lookup is a verification and not a
     * node read.
     */
    static boolean isStatelessId( final String id )
    {
        return id.startsWith( "eyJ" ) && id.indexOf( '.' ) > 0;
    }

    /**
     * @param declared the client as declared, under a placeholder id; a name equal to that id is no
     *                 name, and the client is named after the id it gets
     */
    Client register( final Client declared )
    {
        final Instant now = clock.instant();
        final String name = declared.getDisplayName().equals( declared.getId() ) ? null : declared.getDisplayName();
        final ObjectNode payload = JwsHs512.newPayload();
        payload.put( "idp", declared.getIdProvider().toString() );
        payload.put( "iat", now.getEpochSecond() );
        payload.put( "exp", now.plus( ttl ).getEpochSecond() );
        putIfPresent( payload, "client_name", name );
        putIfPresent( payload, "client_uri", declared.getUri() );
        putIfPresent( payload, "logo_uri", declared.getLogoUri() );
        putIfPresent( payload, "policy_uri", declared.getPolicyUri() );
        putIfPresent( payload, "tos_uri", declared.getTosUri() );
        strings( payload, "grant_types", declared.getGrantTypes() );
        strings( payload, "redirect_uris", declared.getRedirectUris() );
        strings( payload, "scope", declared.getScope() );
        strings( payload, "resource", declared.getResource() );
        final String id = JwsHs512.sign( keys.signingKey(), TYP, payload );
        return withId( declared, id, name );
    }

    /**
     * The client the id describes, if the id provider matches, the signature holds and the
     * registration has not run out.
     */
    Optional<Client> read( final IdProviderKey idProvider, final String id )
    {
        final Optional<JwsHs512.Parsed> parsedOpt = JwsHs512.parse( id );
        if ( parsedOpt.isEmpty() || !JwsHs512.hasExpectedHeader( parsedOpt.get(), TYP ) )
        {
            return Optional.empty();
        }
        final JwsHs512.Parsed parsed = parsedOpt.get();
        final Optional<SigningKey> key = parsed.kid().flatMap( keys::verificationKey );
        if ( key.isEmpty() || !JwsHs512.verify( parsed, key.get(), TYP ) )
        {
            return Optional.empty();
        }
        final ObjectNode payload = parsed.payload();
        final JsonNode exp = payload.get( "exp" );
        if ( !idProvider.toString().equals( text( payload, "idp" ) ) || exp == null || !exp.canConvertToLong() ||
            !clock.instant().isBefore( Instant.ofEpochSecond( exp.asLong() ) ) )
        {
            return Optional.empty();
        }
        try
        {
            return Optional.of( Client.create()
                                    .idProvider( idProvider )
                                    .id( id )
                                    .displayName( text( payload, "client_name" ) )
                                    .uri( text( payload, "client_uri" ) )
                                    .logoUri( text( payload, "logo_uri" ) )
                                    .policyUri( text( payload, "policy_uri" ) )
                                    .tosUri( text( payload, "tos_uri" ) )
                                    .grantTypes( strings( payload, "grant_types" ) )
                                    .redirectUris( strings( payload, "redirect_uris" ) )
                                    .scope( strings( payload, "scope" ) )
                                    .resource( strings( payload, "resource" ) )
                                    .build() );
        }
        catch ( IllegalArgumentException e )
        {
            return Optional.empty();
        }
    }

    private static Client withId( final Client declared, final String id, final String name )
    {
        return Client.create()
            .idProvider( declared.getIdProvider() )
            .id( id )
            .displayName( name )
            .uri( declared.getUri() )
            .logoUri( declared.getLogoUri() )
            .policyUri( declared.getPolicyUri() )
            .tosUri( declared.getTosUri() )
            .grantTypes( declared.getGrantTypes() )
            .redirectUris( declared.getRedirectUris() )
            .scope( declared.getScope() )
            .resource( declared.getResource() )
            .build();
    }

    private static void putIfPresent( final ObjectNode node, final String name, final String value )
    {
        if ( value != null )
        {
            node.put( name, value );
        }
    }

    private static void strings( final ObjectNode node, final String name, final Iterable<String> values )
    {
        final ArrayNode array = node.putArray( name );
        values.forEach( array::add );
    }

    private static String text( final ObjectNode node, final String name )
    {
        final JsonNode value = node.get( name );
        return value != null && value.isTextual() ? value.asText() : null;
    }

    private static List<String> strings( final ObjectNode node, final String name )
    {
        final List<String> values = new ArrayList<>();
        final JsonNode array = node.get( name );
        if ( array != null && array.isArray() )
        {
            array.forEach( value -> {
                if ( value.isTextual() )
                {
                    values.add( value.asText() );
                }
            } );
        }
        return values;
    }
}
