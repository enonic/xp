package com.enonic.xp.portal.url;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.style.StyleDescriptors;

@PublicApi
public class ProcessHtmlUrlGeneratorParams
{
    private final BaseUrlStrategy mediaBaseUrlStrategy;

    private final BaseUrlStrategy pageBaseUrlStrategy;

    private final ProjectName projectName;

    private final Branch branch;

    private final String value;

    private final List<Integer> imageWidths;

    private final String imageSizes;

    private final Function<HtmlProcessorParams, String> customHtmlProcessor;

    private final Supplier<StyleDescriptors> customStyleDescriptors;

    private final boolean processMacros;

    private final Multimap<String, String> queryParams;

    private ProcessHtmlUrlGeneratorParams( final Builder builder )
    {
        this.mediaBaseUrlStrategy = Objects.requireNonNull( builder.mediaBaseUrlStrategy );
        this.pageBaseUrlStrategy = Objects.requireNonNull( builder.pageBaseUrlStrategy );
        this.processMacros = Objects.requireNonNullElse( builder.processMacros, true );
        this.projectName = builder.projectName;
        this.branch = builder.branch;
        this.value = builder.value;
        this.imageWidths = builder.imageWidths;
        this.imageSizes = builder.imageSizes;
        this.customHtmlProcessor = builder.customHtmlProcessor;
        this.customStyleDescriptors = builder.customStyleDescriptors;
        this.queryParams = builder.queryParams;
    }

    public BaseUrlStrategy getMediaBaseUrlStrategy()
    {
        return mediaBaseUrlStrategy;
    }

    public BaseUrlStrategy getPageBaseUrlStrategy()
    {
        return pageBaseUrlStrategy;
    }

    public ProjectName getProjectName()
    {
        return projectName;
    }

    public Branch getBranch()
    {
        return branch;
    }

    public String getValue()
    {
        return value;
    }

    public List<Integer> getImageWidths()
    {
        return imageWidths;
    }

    public String getImageSizes()
    {
        return imageSizes;
    }

    public Function<HtmlProcessorParams, String> getCustomHtmlProcessor()
    {
        return customHtmlProcessor;
    }

    public Supplier<StyleDescriptors> getCustomStyleDescriptors()
    {
        return customStyleDescriptors;
    }

    public Map<String, Collection<String>> getQueryParams()
    {
        return queryParams.asMap();
    }

    public boolean isProcessMacros()
    {
        return processMacros;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private BaseUrlStrategy mediaBaseUrlStrategy;

        private BaseUrlStrategy pageBaseUrlStrategy;

        private ProjectName projectName;

        private Branch branch;

        private String value;

        private List<Integer> imageWidths;

        private String imageSizes;

        private Function<HtmlProcessorParams, String> customHtmlProcessor;

        private Supplier<StyleDescriptors> customStyleDescriptors;

        private Boolean processMacros;

        private final Multimap<String, String> queryParams = LinkedListMultimap.create();

        public Builder setMediaBaseUrlStrategy( final BaseUrlStrategy mediaBaseUrlStrategy )
        {
            this.mediaBaseUrlStrategy = mediaBaseUrlStrategy;
            return this;
        }

        public Builder setPageBaseUrlStrategy( final BaseUrlStrategy pageBaseUrlStrategy )
        {
            this.pageBaseUrlStrategy = pageBaseUrlStrategy;
            return this;
        }

        public Builder setProjectName( final ProjectName projectName )
        {
            this.projectName = projectName;
            return this;
        }

        public Builder setBranch( final Branch branch )
        {
            this.branch = branch;
            return this;
        }

        public Builder setValue( final String value )
        {
            this.value = value;
            return this;
        }

        public Builder setImageWidths( final List<Integer> imageWidths )
        {
            this.imageWidths = imageWidths;
            return this;
        }

        public Builder setImageSizes( final String imageSizes )
        {
            this.imageSizes = imageSizes;
            return this;
        }

        public Builder setCustomHtmlProcessor( final Function<HtmlProcessorParams, String> customHtmlProcessor )
        {
            this.customHtmlProcessor = customHtmlProcessor;
            return this;
        }

        public Builder setCustomStyleDescriptors( final Supplier<StyleDescriptors> customStyleDescriptors )
        {
            this.customStyleDescriptors = customStyleDescriptors;
            return this;
        }

        public Builder setProcessMacros( final Boolean processMacros )
        {
            this.processMacros = processMacros;
            return this;
        }

        public Builder addQueryParams( final Map<String, Collection<String>> queryParams )
        {
            queryParams.forEach( this.queryParams::putAll );
            return this;
        }

        public Builder addQueryParam( final String key, final String value )
        {
            this.queryParams.put( key, value );
            return this;
        }

        public ProcessHtmlUrlGeneratorParams build()
        {
            return new ProcessHtmlUrlGeneratorParams( this );
        }
    }
}
