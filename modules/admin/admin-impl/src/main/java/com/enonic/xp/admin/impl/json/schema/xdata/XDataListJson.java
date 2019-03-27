package com.enonic.xp.admin.impl.json.schema.xdata;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import com.enonic.xp.admin.impl.rest.resource.schema.content.LocaleMessageResolver;
import com.enonic.xp.admin.impl.rest.resource.schema.mixin.InlineMixinResolver;
import com.enonic.xp.admin.impl.rest.resource.schema.mixin.MixinIconUrlResolver;
import com.enonic.xp.schema.xdata.XDatas;

public class XDataListJson
{
    private final List<XDataJson> list;

    public XDataListJson()
    {
        this.list = new ArrayList<>();
    }

    public XDataListJson( final Builder builder )
    {
        this.list = builder.xDatas.stream().map( xData -> XDataJson.
            create().
            setXData( xData ).
            setIconUrlResolver( builder.iconUrlResolver ).
            setLocaleMessageResolver( builder.localeMessageResolver ).
            setInlineMixinResolver( builder.inlineMixinResolver ).
            build() ).collect( Collectors.toList() );
    }

    public void addXDatas( final List<XDataJson> xDatas )
    {
        this.list.addAll( xDatas );
    }

    public List<XDataJson> getXDatas()
    {
        return this.list;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private XDatas xDatas = XDatas.empty();

        private MixinIconUrlResolver iconUrlResolver;

        private LocaleMessageResolver localeMessageResolver;

        private InlineMixinResolver inlineMixinResolver;

        private Builder()
        {
        }

        public Builder setXDatas( final XDatas xDatas )
        {
            this.xDatas = xDatas;
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

        public Builder setInlineMixinResolver( final InlineMixinResolver inlineMixinResolver )
        {
            this.inlineMixinResolver = inlineMixinResolver;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( localeMessageResolver );
            Preconditions.checkNotNull( iconUrlResolver );
            Preconditions.checkNotNull( inlineMixinResolver );
        }

        public XDataListJson build()
        {
            validate();
            return new XDataListJson( this );
        }
    }
}
