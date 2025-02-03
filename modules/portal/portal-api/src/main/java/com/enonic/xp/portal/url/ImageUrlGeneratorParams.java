package com.enonic.xp.portal.url;

import java.util.function.Supplier;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Media;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.site.Site;

public class ImageUrlGeneratorParams
{
    public BaseUrlStrategy baseUrlStrategy;

    public PathPrefixStrategy pathPrefixStrategy;

    public RewritePathStrategy rewritePathStrategy;

    public Supplier<Site> nearestSiteProvider;

    public Supplier<Media> mediaProvider;

    public ProjectName projectName;

    public Branch branch;

    public String scale;

    public String id;

    public String path;

    public String background;

    public Integer quality;

    public String filter;

    public String format;
}
