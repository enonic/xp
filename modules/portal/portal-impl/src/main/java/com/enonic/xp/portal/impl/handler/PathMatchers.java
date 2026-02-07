package com.enonic.xp.portal.impl.handler;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.enonic.xp.web.WebRequest;

public class PathMatchers
{
    public static final String API_BASE = "/api";

    public static final String API_PREFIX = "/api/";

    public static final String WEBAPP_PREFIX = "/webapp/";

    public static final String SITE_BASE = "/site";

    public static final String SITE_PREFIX = SITE_BASE + "/";

    public static final String ADMIN_SITE_PREFIX = "/admin/site/";

    public static final String ADMIN_TOOL_BASE = "/admin";

    public static final String ADMIN_TOOL_PREFIX = ADMIN_TOOL_BASE + "/";

    private static final Pattern ADMIN_TOOL_PATH_PATTERN = Pattern.compile( "^(?<base>/admin/(?<app>[^/]+)/(?<tool>[^/]+))(?<path>.*)" );

    private static final Pattern WEBAPP_PATH_PATTERN = Pattern.compile( "^(?<base>/webapp/(?<app>[^/]+))(?<path>.*)" );

    private static final Pattern SITE_PATH_PATTERN = Pattern.compile( "^(?<base>/site)/(?<project>[^/]+)/(?<branch>[^/]+)(?<path>.*)" );

    private static final Pattern ADMIN_SITE_PATH_PATTERN =
        Pattern.compile( "^(?<base>/admin/site/(?<mode>edit|preview|admin|inline))/(?<project>[^/]+)/(?<branch>[^/]+)(?<path>.*)" );

    private static final Pattern API_PATH_PATTERN =
        Pattern.compile( "^(?<base>/api/(?<descriptor>(?<app>[^/]+):(?<api>[^/?]+)))(?<path>.*)" );

    public static MatchResult webapp( final WebRequest request )
    {
        final Matcher matcher = WEBAPP_PATH_PATTERN.matcher( request.getBasePath() );
        matcher.matches();
        return matcher.toMatchResult();
    }

    public static MatchResult adminTool( final WebRequest request )
    {
        final Matcher matcher = ADMIN_TOOL_PATH_PATTERN.matcher( request.getBasePath() );
        matcher.matches();
        return matcher.toMatchResult();
    }

    public static MatchResult site( final WebRequest request )
    {
        final Matcher matcher = SITE_PATH_PATTERN.matcher( request.getBasePath() );
        matcher.matches();
        return matcher.toMatchResult();
    }

    public static MatchResult adminSite( final WebRequest request )
    {
        final Matcher matcher = ADMIN_SITE_PATH_PATTERN.matcher( request.getBasePath() );
        matcher.matches();
        return matcher.toMatchResult();
    }

    public static MatchResult api( final WebRequest request )
    {
        final Matcher matcher = API_PATH_PATTERN.matcher( request.getBasePath() );
        matcher.matches();
        return matcher.toMatchResult();
    }

    private PathMatchers()
    {
    }
}
