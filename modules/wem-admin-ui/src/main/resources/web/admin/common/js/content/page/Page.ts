module api.content.page {

    export class Page implements api.Equitable, api.Cloneable {

        private controller: DescriptorKey;

        private template: PageTemplateKey;

        private regions: PageRegions;

        private config: api.data.RootDataSet;

        constructor(builder: PageBuilder) {
            this.controller = builder.controller;
            this.template = builder.template;
            this.regions = builder.regions;
            this.config = builder.config;
        }

        hasController(): boolean {
            return !!this.controller;
        }

        getController(): DescriptorKey {
            return this.controller;
        }

        hasTemplate(): boolean {
            return !!this.template;
        }

        getTemplate(): PageTemplateKey {
            return this.template;
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

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, Page)) {
                return false;
            }

            var other = <Page>o;

            if (!api.ObjectHelper.equals(this.controller, other.controller)) {
                return false;
            }
            if (!api.ObjectHelper.equals(this.template, other.template)) {
                return false;
            }
            if (!api.ObjectHelper.equals(this.regions, other.regions)) {
                return false;
            }
            if (!api.ObjectHelper.equals(this.config, other.config)) {
                return false;
            }

            return true;
        }

        clone(): Page {

            return new PageBuilder(this).build();
        }
    }

    export class PageBuilder {

        controller: DescriptorKey;

        template: PageTemplateKey;

        regions: PageRegions;

        config: api.data.RootDataSet;

        constructor(source?: Page) {
            if (source) {
                this.controller = source.getController();
                this.template = source.getTemplate();
                this.regions = source.getRegions() ? source.getRegions().clone() : null;
                this.config = source.getConfig() ? source.getConfig().clone() : null;
            }
        }

        public fromJson(json: api.content.page.PageJson): PageBuilder {
            this.setController(json.controller ? DescriptorKey.fromString(json.controller) : null);
            this.setTemplate(json.template ? PageTemplateKey.fromString(json.template) : null);
            this.setRegions(json.regions != null ? new PageRegionsBuilder().fromJson(json.regions).build() : null);
            this.setConfig(json.config != null ? api.data.DataFactory.createRootDataSet(json.config) : null);
            return this;
        }

        public setController(value: DescriptorKey): PageBuilder {
            this.controller = value;
            return this;
        }

        public setTemplate(value: PageTemplateKey): PageBuilder {
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