module api.content.page {

    export class PageTemplate extends PageTemplateSummary {

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

        isCanRender(pattern:api.schema.content.ContentTypeName): boolean {
            var result = false;
            this.canRender.some((name:api.schema.content.ContentTypeName)=> {
                if (name.equals(pattern)) {
                    result = true;
                    return true;
                }
                else {
                    return false;
                }
            });
            return result;
        }
    }

    export class PageTemplateBuilder extends PageTemplateSummaryBuilder {

        key: PageTemplateKey;

        displayName: string;

        descriptorKey: DescriptorKey;

        regions: PageRegions;

        config: api.data.RootDataSet;

        canRender: api.schema.content.ContentTypeName[] = [];

        fromJson(json: api.content.page.json.PageTemplateJson): PageTemplateBuilder {

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