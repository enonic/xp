package com.enonic.wem.web.filter.bundle;

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

public final class BundleModel
{
    private String namespace;

    private List<String> jsPaths;

    private List<String> templatePaths;

    private List<String> i18nPaths;

    public String getNamespace()
    {
        return namespace;
    }

    @JsonProperty("ns")
    public void setNamespace( final String namespace )
    {
        this.namespace = namespace;
    }

    public List<String> getJsPaths()
    {
        return jsPaths;
    }

    @JsonProperty("js")
    public void setJsPaths( final List<String> jsPaths )
    {
        this.jsPaths = jsPaths;
    }

    public List<String> getTemplatePaths()
    {
        return templatePaths;
    }

    @JsonProperty("template")
    public void setTemplatePaths( final List<String> templatePaths )
    {
        this.templatePaths = templatePaths;
    }

    public List<String> getI18nPaths()
    {
        return i18nPaths;
    }

    @JsonProperty("i18n")
    public void setI18nPaths( final List<String> i18nPaths )
    {
        this.i18nPaths = i18nPaths;
    }
}
