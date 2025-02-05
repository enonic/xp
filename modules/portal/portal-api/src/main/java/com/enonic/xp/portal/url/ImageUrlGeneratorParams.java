package com.enonic.xp.portal.url;

import java.util.function.Supplier;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Media;
import com.enonic.xp.project.ProjectName;

public class ImageUrlGeneratorParams
{
    public BaseUrlStrategy baseUrlStrategy;

    public PathPrefixStrategy pathPrefixStrategy;

    public RewritePathStrategy rewritePathStrategy;

    public Supplier<Media> mediaProvider;

    public ProjectName projectName;

    public Branch branch;

    public String scale;

    public String background;

    public Integer quality;

    public String filter;

    public String format;
}
