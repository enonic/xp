package com.enonic.xp.lib.portal.csp;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.csp.ContentSecurityPolicy;
import com.enonic.xp.portal.csp.HashAlgo;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

import static java.util.Objects.requireNonNull;

public final class CspHandler
    implements ScriptBean
{
    private PortalRequest request;

    public void add( final String directive, final ScriptValue sources )
    {
        policy().add( directive, toSources( sources ) );
    }

    public void set( final String directive, final ScriptValue sources )
    {
        policy().set( directive, toSources( sources ) );
    }

    public void addShaContent( final String directive, final String content )
    {
        requireNonNull( content, "content is required" );
        policy().addSha( directive, content.getBytes( StandardCharsets.UTF_8 ) );
    }

    public void addShaDigest( final String directive, final String base64, final String algo )
    {
        policy().addSha( directive, parseAlgo( algo ), base64 );
    }

    public String getNonce()
    {
        return policy().nonce();
    }

    public String build()
    {
        return policy().build();
    }

    private ContentSecurityPolicy policy()
    {
        return this.request.getContentSecurityPolicy();
    }

    private static String[] toSources( final ScriptValue value )
    {
        if ( value == null )
        {
            return new String[0];
        }
        if ( value.isArray() )
        {
            final List<String> list = value.getArray( String.class );
            return list.toArray( new String[0] );
        }
        if ( value.isValue() )
        {
            return new String[]{value.getValue( String.class )};
        }
        throw new IllegalArgumentException( "Expected a string or an array of strings" );
    }

    private static HashAlgo parseAlgo( final String algo )
    {
        if ( algo == null )
        {
            return HashAlgo.SHA256;
        }
        switch ( algo.toLowerCase( Locale.ROOT ) )
        {
            case "sha256":
                return HashAlgo.SHA256;
            case "sha384":
                return HashAlgo.SHA384;
            case "sha512":
                return HashAlgo.SHA512;
            default:
                throw new IllegalArgumentException( "Unsupported hash algorithm: " + algo + ". Expected one of: sha256, sha384, sha512" );
        }
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.request = requireNonNull( context.getBinding( PortalRequest.class ).get(), "no request bound" );
    }
}
