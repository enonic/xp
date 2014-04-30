module api.content.page {

    export class PageTemplate extends PageTemplateSummary implements api.Equitable {

        private regions: PageRegions;

        private config: api.data.RootDataSet;

        private canRender: api.schema.content.ContentTypeName[];

        constructor(builder: PageTemplateBuilder) {
            super(builder);
            this.regions = builder.regions;
            this.config = builder.config;
            this.canRender = builder.canRender;
        }

        hasRegions(): boolean {
            return this.regions != null;
        }

        getRegions(): PageRegions {
            return this.regions;
        }

        getConfig(): api.data.RootDataSet {
            return this.config;
        }

        getCanRender(): api.schema.content.ContentTypeName[] {
            return this.canRender;
        }

        isCanRender(pattern: api.schema.content.ContentTypeName): boolean {
            return this.canRender.some((name: api.schema.content.ContentTypeName)=> {
                return name.equals(pattern);
            });
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, PageTemplate)) {
                return false;
            }

            if (!super.equals(o)) {
                return false;
            }

            var other = <PageTemplate>o;

            if (!api.ObjectHelper.equals(this.regions, other.regions)) {
                return false;
            }

            if (!api.ObjectHelper.equals(this.config, other.config)) {
                return false;
            }

            if (!api.ObjectHelper.arrayEquals(this.canRender, other.canRender)) {
                return false;
            }

            return true;
        }
    }

    export class PageTemplateBuilder extends PageTemplateSummaryBuilder {

        key: PageTemplateKey;

        displayName: string;

        descriptorKey: DescriptorKey;

        regions: PageRegions;

        config: api.data.RootDataSet;

        canRender: api.schema.content.ContentTypeName[] = [];

        fromJson(json: api.content.page.PageTemplateJson): PageTemplateBuilder {

            this.setKey(PageTemplateKey.fromString(json.key));
            this.setDisplayName(json.displayName);
            this.setDescriptorKey(DescriptorKey.fromString(json.descriptorKey));
            this.setConfig(api.data.DataFactory.createRootDataSet(json.config));
            this.setRegions(new PageRegionsBuilder().fromJson(json.regions).build());
            json.canRender.forEach((name: string)=> {
                this.canRender.push(new api.schema.content.ContentTypeName(name))
            });
            return this;
        }

        public setKey(value: PageTemplateKey): PageTemplateBuilder {
            this.key = value;
            return this;
        }

        public setDisplayName(value: string): PageTemplateBuilder {
            this.displayName = value;
            return this;
        }

        public setDescriptorKey(value: DescriptorKey): PageTemplateBuilder {
            this.descriptorKey = value;
            return this;
        }

        public setConfig(value: api.data.RootDataSet): PageTemplateBuilder {
            this.config = value;
            return this;
        }

        public setRegions(value: PageRegions): PageTemplateBuilder {
            this.regions = value;
            return this;
        }

        setCanRender(value: api.schema.content.ContentTypeName[]): PageTemplateBuilder {
            this.canRender = value;
            return this;
        }

        public build(): PageTemplate {
            return new PageTemplate(this);
        }
    }
}