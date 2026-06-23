package com.enonic.xp.core.impl.security.token;

import java.time.Instant;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.security.CryptoService;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.token.AccessToken;
import com.enonic.xp.security.token.AccessTokenParams;
import com.enonic.xp.security.token.AccessTokenService;

@Component(service = AccessTokenService.class)
@NullMarked
public class AccessTokenServiceImpl
    implements AccessTokenService
{
    private final CryptoService cryptoService;

    @Activate
    public AccessTokenServiceImpl( @Reference final CryptoService cryptoService )
    {
        this.cryptoService = cryptoService;
    }

    @Override
    public String issue( final AccessTokenParams params )
    {
        if ( !params.getSubject().isUser() )
        {
            throw new IllegalArgumentException( "Access token subject must be a user: " + params.getSubject() );
        }

        final String kid = cryptoService.tokenSigningKeyId();
        final SecretKey key = cryptoService.getSigningKey( kid );

        final Map<String, Object> header = new LinkedHashMap<>();
        header.put( "alg", "HS512" );
        header.put( "typ", "at+jwt" );
        header.put( "kid", kid );

        final Instant now = Instant.now();
        final Map<String, Object> claims = new LinkedHashMap<>();
        claims.put( "iss", params.getIssuer() );
        claims.put( "sub", params.getSubject().toString() );
        if ( !params.getAudiences().isEmpty() )
        {
            claims.put( "aud", params.getAudiences() );
        }
        if ( params.getClientId() != null )
        {
            claims.put( "client_id", params.getClientId() );
        }
        if ( params.getScope() != null )
        {
            claims.put( "scope", params.getScope() );
        }
        claims.put( "iat", now.getEpochSecond() );
        claims.put( "exp", now.plusSeconds( params.getTtlSeconds() ).getEpochSecond() );
        claims.put( "jti", UUID.randomUUID().toString() );

        return JwsHs512.sign( header, claims, key );
    }

    @Override
    public Optional<AccessToken> verify( @Nullable final String token )
    {
        if ( token == null )
        {
            return Optional.empty();
        }

        try
        {
            final Map<String, Object> header = JwsHs512.peekSegment( token, 0 );
            if ( header == null )
            {
                return Optional.empty();
            }

            // The algorithm is pinned to HS512 - the token header is never trusted to choose it.
            if ( !"HS512".equals( header.get( "alg" ) ) )
            {
                return Optional.empty();
            }

            final Object kid = header.get( "kid" );
            if ( !( kid instanceof String ) )
            {
                return Optional.empty();
            }

            final SecretKey key = cryptoService.getSigningKey( (String) kid );

            final Map<String, Object> claims = JwsHs512.verify( token, key );
            if ( claims == null )
            {
                return Optional.empty();
            }

            final Object iss = claims.get( "iss" );
            final Object sub = claims.get( "sub" );
            final Object exp = claims.get( "exp" );
            if ( !( iss instanceof String ) || !( sub instanceof String ) || !( exp instanceof Number ) )
            {
                return Optional.empty();
            }

            final Instant expiresAt = Instant.ofEpochSecond( ( (Number) exp ).longValue() );
            if ( Instant.now().minusSeconds( 1 ).isAfter( expiresAt ) )
            {
                return Optional.empty();
            }

            final PrincipalKey subjectKey = PrincipalKey.from( (String) sub );
            if ( !subjectKey.isUser() )
            {
                return Optional.empty();
            }

            // The id provider is part of the subject PrincipalKey; it is not stored separately.
            return Optional.of( AccessToken.create()
                                    .subject( subjectKey )
                                    .issuer( (String) iss )
                                    .audiences( toStringSet( claims.get( "aud" ) ) )
                                    .expiresAt( expiresAt )
                                    .claims( claims )
                                    .build() );
        }
        catch ( Exception e )
        {
            return Optional.empty();
        }
    }

    private static Set<String> toStringSet( @Nullable final Object audience )
    {
        if ( audience instanceof String )
        {
            return Set.of( (String) audience );
        }
        if ( audience instanceof Collection )
        {
            return ( (Collection<?>) audience ).stream().map( String::valueOf ).collect( Collectors.toUnmodifiableSet() );
        }
        return Set.of();
    }
}
