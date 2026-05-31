package com.enonic.xp.lib.portal.csp;

import java.nio.charset.StandardCharsets;

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

    public void override( final String directive, final String[] sources )
    {
        policy().override( directive, sources );
    }

    public void reset( final String[] directives )
    {
        policy().reset( directives );
    }

    public void strict()
    {
        policy().strict();
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

    public void scriptSrcElem( final String[] sources )
    {
        policy().scriptSrcElem( sources );
    }

    public void scriptSrcAttr( final String[] sources )
    {
        policy().scriptSrcAttr( sources );
    }

    public void styleSrcElem( final String[] sources )
    {
        policy().styleSrcElem( sources );
    }

    public void styleSrcAttr( final String[] sources )
    {
        policy().styleSrcAttr( sources );
    }

    public void reportTo( final String group )
    {
        policy().reportTo( group );
    }

    public void requireTrustedTypesFor()
    {
        policy().requireTrustedTypesFor();
    }

    public void trustedTypes( final String[] values )
    {
        policy().trustedTypes( values );
    }

    public void reportOnly( final boolean value )
    {
        policy().reportOnly( value );
    }

    public void sandbox( final String[] flags )
    {
        policy().add( "sandbox", flags );
    }

    public void scriptSrcShaContent( final String content, final String algo )
    {
        requireNonNull( content, "content is required" );
        policy().scriptSrcSha( parseAlgo( algo ), content.getBytes( StandardCharsets.UTF_8 ) );
    }

    public void scriptSrcShaDigest( final String base64, final String algo )
    {
        policy().scriptSrcSha( parseAlgo( algo ), base64 );
    }

    public void styleSrcShaContent( final String content, final String algo )
    {
        requireNonNull( content, "content is required" );
        policy().styleSrcSha( parseAlgo( algo ), content.getBytes( StandardCharsets.UTF_8 ) );
    }

    public void styleSrcShaDigest( final String base64, final String algo )
    {
        policy().styleSrcSha( parseAlgo( algo ), base64 );
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
        switch ( algo )
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
