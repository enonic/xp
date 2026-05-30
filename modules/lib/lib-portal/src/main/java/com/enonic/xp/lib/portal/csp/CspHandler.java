package com.enonic.xp.lib.portal.csp;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.csp.ContentSecurityPolicy;
import com.enonic.xp.portal.csp.HashAlgo;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

import static java.util.Objects.requireNonNull;

public final class CspHandler
    implements ScriptBean
{
    private PortalRequest request;

    public void add( final String directive, final String[] sources )
    {
        policy().add( directive, sources );
    }

    public void set( final String directive, final String[] sources )
    {
        policy().set( directive, sources );
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

    public void defaultSrc( final String[] sources )
    {
        policy().defaultSrc( sources );
    }

    public void scriptSrc( final String[] sources )
    {
        policy().scriptSrc( sources );
    }

    public void styleSrc( final String[] sources )
    {
        policy().styleSrc( sources );
    }

    public void imgSrc( final String[] sources )
    {
        policy().imgSrc( sources );
    }

    public void fontSrc( final String[] sources )
    {
        policy().fontSrc( sources );
    }

    public void connectSrc( final String[] sources )
    {
        policy().connectSrc( sources );
    }

    public void mediaSrc( final String[] sources )
    {
        policy().mediaSrc( sources );
    }

    public void objectSrc( final String[] sources )
    {
        policy().objectSrc( sources );
    }

    public void frameSrc( final String[] sources )
    {
        policy().frameSrc( sources );
    }

    public void workerSrc( final String[] sources )
    {
        policy().workerSrc( sources );
    }

    public void manifestSrc( final String[] sources )
    {
        policy().manifestSrc( sources );
    }

    public void childSrc( final String[] sources )
    {
        policy().childSrc( sources );
    }

    public void frameAncestors( final String[] sources )
    {
        policy().frameAncestors( sources );
    }

    public void baseUri( final String[] sources )
    {
        policy().baseUri( sources );
    }

    public void formAction( final String[] sources )
    {
        policy().formAction( sources );
    }

    public void upgradeInsecureRequests()
    {
        policy().upgradeInsecureRequests();
    }

    public void sandbox( final String[] flags )
    {
        policy().add( "sandbox", flags );
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
