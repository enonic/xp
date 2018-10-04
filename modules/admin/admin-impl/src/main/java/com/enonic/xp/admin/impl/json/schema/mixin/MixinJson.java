package com.enonic.xp.admin.impl.json.schema.mixin;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Preconditions;

import com.enonic.xp.admin.impl.json.ItemJson;
import com.enonic.xp.admin.impl.json.form.FormJson;
import com.enonic.xp.admin.impl.rest.resource.schema.content.LocaleMessageResolver;
import com.enonic.xp.admin.impl.rest.resource.schema.mixin.MixinIconUrlResolver;
import com.enonic.xp.schema.mixin.Mixin;

public class MixinJson
    implements ItemJson
{
    private final Mixin mixin;

    private final String iconUrl;

    private final LocaleMessageResolver localeMessageResolver;

    public MixinJson( final Builder builder )
    {
        Preconditions.checkNotNull( builder.localeMessageResolver );

        this.mixin = builder.mixin;
        this.iconUrl = builder.iconUrlResolver.resolve( mixin );
        this.localeMessageResolver = builder.localeMessageResolver;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public String getName()
    {
        return mixin.getName() != null ? mixin.getName().toString() : null;
    }

    public String getDisplayName()
    {
        if ( StringUtils.isNotBlank( mixin.getDisplayNameI18nKey() ) )
        {
            return localeMessageResolver.localizeMessage( mixin.getDisplayNameI18nKey(), mixin.getDisplayName() );
        }
        else
        {
            return mixin.getDisplayName();
        }
    }

    public String getDescription()
    {
        if ( StringUtils.isNotBlank( mixin.getDescriptionI18nKey() ) )
        {
            return localeMessageResolver.localizeMessage( mixin.getDescriptionI18nKey(), mixin.getDescription() );
        }
        else
        {
            return mixin.getDescription();
        }
    }

    public Instant getCreatedTime()
    {
        return mixin.getCreatedTime();
    }

    public Instant getModifiedTime()
    {
        return mixin.getModifiedTime();
    }

    public String getIconUrl()
    {
        return iconUrl;
    }

    public FormJson getForm()
    {
        return new FormJson( mixin.getForm(), this.localeMessageResolver );
    }

    public String getCreator()
    {
        return mixin.getCreator() != null ? mixin.getCreator().toString() : null;
    }

    public String getModifier()
    {
        return mixin.getModifier() != null ? mixin.getModifier().toString() : null;
    }

    public List<String> getAllowedContentTypes()
    {
        return Collections.emptyList();
    }

    @Override
    public boolean getDeletable()
    {
        return false;
    }

    @Override
    public boolean getEditable()
    {
        return false;
    }

    public static final class Builder
    {
        private Mixin mixin;

        private MixinIconUrlResolver iconUrlResolver;

        private LocaleMessageResolver localeMessageResolver;

        private Builder()
        {
        }

        public Builder setMixin( final Mixin mixin )
        {
            this.mixin = mixin;
            return this;
        }

        public Builder setIconUrlResolver( final MixinIconUrlResolver iconUrlResolver )
        {
            this.iconUrlResolver = iconUrlResolver;
            return this;
        }

        public Builder setLocaleMessageResolver( final LocaleMessageResolver localeMessageResolver )
        {
            this.localeMessageResolver = localeMessageResolver;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( localeMessageResolver );
            Preconditions.checkNotNull( iconUrlResolver );
        }

        public MixinJson build()
        {
            validate();
            return new MixinJson( this );
        }
    }

}
