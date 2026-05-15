package com.enonic.xp.project;

import java.util.Locale;

import org.jspecify.annotations.NonNull;

import com.enonic.xp.site.SiteConfigs;

import static java.util.Objects.requireNonNull;


public final class EditableProject
{
    public final @NonNull Project source;

    public String displayName;

    public String description;

    public Locale language;

    public SiteConfigs siteConfigs;

    public EditableProject( final Project source )
    {
        this.source = requireNonNull( source );
        this.displayName = source.getDisplayName();
        this.description = source.getDescription();
        this.language = source.getLanguage();
        this.siteConfigs = source.getSiteConfigs();
    }
}
