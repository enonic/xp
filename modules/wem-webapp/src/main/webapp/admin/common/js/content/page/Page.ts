module api.content.page {

    export class Page {

        private template: TemplateKey;

        private regions: PageRegions;

        private config: api.data.RootDataSet;

        constructor(builder: PageBuilder) {
            this.template = builder.template;
            this.regions = builder.regions;
            this.config = builder.config;
        }

        getTemplate(): TemplateKey {
            return this.template;
        }

        setTemplate(template: TemplateKey) {
            this.template = template;
        }

        hasRegions(): boolean {
            return this.regions != null;
        }

        getRegions(): PageRegions {
            return this.regions;
        }

        hasConfig(): boolean {
            return this.config != null;
        }

        getConfig(): api.data.RootDataSet {
            return this.config;
        }
    }

    export class PageBuilder {

        template: TemplateKey;

        regions: PageRegions;

        config: api.data.RootDataSet;

        public fromJson(json: api.content.page.json.PageJson): PageBuilder {
            this.setTemplate(TemplateKey.fromString(json.template));
            this.setRegions(json.regions != null ? new PageRegionsBuilder().fromJson(json.regions).build() : null);
            this.setConfig(json.config != null ? api.data.DataFactory.createRootDataSet(json.config) : null);
            return this;
        }

        public setTemplate(value: TemplateKey): PageBuilder {
            this.template = value;
            return this;
        }

        public setRegions(value: PageRegions): PageBuilder {
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