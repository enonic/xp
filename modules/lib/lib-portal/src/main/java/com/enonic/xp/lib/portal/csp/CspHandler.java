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

    public void strict()
    {
        policy().strict();
    }

    public void strictDynamic()
    {
        policy().strictDynamic();
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

    public void defaultSrc( final ScriptValue sources )
    {
        policy().defaultSrc( toSources( sources ) );
    }

    public void scriptSrc( final ScriptValue sources )
    {
        policy().scriptSrc( toSources( sources ) );
    }

    public void styleSrc( final ScriptValue sources )
    {
        policy().styleSrc( toSources( sources ) );
    }

    public void imgSrc( final ScriptValue sources )
    {
        policy().imgSrc( toSources( sources ) );
    }

    public void fontSrc( final ScriptValue sources )
    {
        policy().fontSrc( toSources( sources ) );
    }

    public void connectSrc( final ScriptValue sources )
    {
        policy().connectSrc( toSources( sources ) );
    }

    public void mediaSrc( final ScriptValue sources )
    {
        policy().mediaSrc( toSources( sources ) );
    }

    public void objectSrc( final ScriptValue sources )
    {
        policy().objectSrc( toSources( sources ) );
    }

    public void frameSrc( final ScriptValue sources )
    {
        policy().frameSrc( toSources( sources ) );
    }

    public void workerSrc( final ScriptValue sources )
    {
        policy().workerSrc( toSources( sources ) );
    }

    public void manifestSrc( final ScriptValue sources )
    {
        policy().manifestSrc( toSources( sources ) );
    }

    public void childSrc( final ScriptValue sources )
    {
        policy().childSrc( toSources( sources ) );
    }

    public void frameAncestors( final ScriptValue sources )
    {
        policy().frameAncestors( toSources( sources ) );
    }

    public void baseUri( final ScriptValue sources )
    {
        policy().baseUri( toSources( sources ) );
    }

    public void formAction( final ScriptValue sources )
    {
        policy().formAction( toSources( sources ) );
    }

    public void upgradeInsecureRequests()
    {
        policy().upgradeInsecureRequests();
    }

    public void sandbox( final ScriptValue flags )
    {
        policy().add( "sandbox", toSources( flags ) );
    }

    public void addScriptSrcShaContent( final String content )
    {
        requireNonNull( content, "content is required" );
        policy().addScriptSrcSha( content.getBytes( StandardCharsets.UTF_8 ) );
    }

    public void addScriptSrcShaDigest( final String base64, final String algo )
    {
        policy().addScriptSrcSha( parseAlgo( algo ), base64 );
    }

    public void addStyleSrcShaContent( final String content )
    {
        requireNonNull( content, "content is required" );
        policy().addStyleSrcSha( content.getBytes( StandardCharsets.UTF_8 ) );
    }

    public void addStyleSrcShaDigest( final String base64, final String algo )
    {
        policy().addStyleSrcSha( parseAlgo( algo ), base64 );
    }

    public String nonce()
    {
        return policy().nonce();
    }

    public String nonceScriptSrc()
    {
        return policy().nonceScriptSrc();
    }

    public String nonceStyleSrc()
    {
        return policy().nonceStyleSrc();
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
