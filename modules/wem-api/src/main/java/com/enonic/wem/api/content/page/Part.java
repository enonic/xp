package com.enonic.wem.api.content.page;


import com.enonic.wem.api.data.RootDataSet;

public final class Part
    extends PageComponent<PartTemplateId>
{
    private final RootDataSet config;

    private final RootDataSet liveEditConfig;

    public Part( final Builder builder )
    {
        super( builder.partTemplateId );
        this.liveEditConfig = builder.liveEditConfig;
        this.config = builder.config;
    }

    public RootDataSet getLiveEditConfig()
    {
        return liveEditConfig;
    }

    public RootDataSet getConfig()
    {
        return config;
    }

    public static Builder newPart()
    {
        return new Builder();
    }

    public static class Builder
    {
        private RootDataSet config;

        private RootDataSet liveEditConfig;

        private PartTemplateId partTemplateId;

        private Builder()
        {
            this.config = RootDataSet.newDataSet().build().toRootDataSet();
            this.liveEditConfig = RootDataSet.newDataSet().build().toRootDataSet();
        }

        public Builder partConfig( final RootDataSet config )
        {
            this.config = config;
            return this;
        }

        public Builder liveEditConfig( final RootDataSet config )
        {
            this.liveEditConfig = config;
            return this;
        }

        public Builder partTemplateId( final PartTemplateId partTemplateId )
        {
            this.partTemplateId = partTemplateId;
            return this;
        }

        public Part build()
        {
            return new Part( this );
        }
    }
}
