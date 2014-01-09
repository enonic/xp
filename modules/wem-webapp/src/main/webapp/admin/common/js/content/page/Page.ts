module api.content.page {

    export class Page extends PageComponent<PageTemplateKey> {

        private regions: region.PageRegions;

        private config: api.data.RootDataSet;

        constructor(builder: PageBuilder) {
            super(builder);
            this.regions = builder.regions;
            this.config = builder.config;
        }

        hasRegions(): boolean {
            return this.regions != null;
        }

        getRegions(): region.PageRegions {
            return this.regions;
        }

        hasConfig(): boolean {
            return this.config != null;
        }

        getConfig(): api.data.RootDataSet {
            return this.config;
        }
    }

    export class PageBuilder extends ComponentBuilder<PageTemplateKey> {

        regions: region.PageRegions;

        config: api.data.RootDataSet;

        public fromJson(json: api.content.page.json.PageJson): PageBuilder {
            this.setTemplate(PageTemplateKey.fromString(json.template));
            this.setRegions(new region.PageRegionsBuilder().fromJson(json.regions).build());
            this.setConfig(api.data.DataFactory.createRootDataSet(json.config));
            return this;
        }

        public setRegions(value: region.PageRegions): PageBuilder {
            this.regions = value;
            return this;
        }

        public setConfig(value: api.data.RootDataSet): PageBuilder {
            this.config = value;
            return this;
        }

        public build(): Page {
            return new Page(this);
        }
    }
}