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

        getController(): DescriptorKey {
            return this.controller;
        }

        setController(value: DescriptorKey) {
            this.controller = value;
        }

        getTemplate(): PageTemplateKey {
            return this.template;
        }

        setTemplate(template: PageTemplateKey) {
            this.template = template;
        }

        hasRegions(): boolean {
            return this.regions != null;
        }

        getRegions(): PageRegions {
            return this.regions;
        }

        setRegions(value: PageRegions) {
            this.regions = value;
        }

        /**
         * Keeps existing regions (including components) if they are listed in given regionDescriptors.
         * Removes others and add those missing.
         * @param regionDescriptors
         */
        changeRegionsTo(regionDescriptors: region.RegionDescriptor[]) {

            // Remove regions not existing in regionDescriptors
            var regionsToRemove: region.Region[] = this.regions.getRegions().
                filter((region: region.Region, index: number) => {
                    return !regionDescriptors.
                        some((regionDescriptor: region.RegionDescriptor) => {
                            return regionDescriptor.getName() == region.getName();
                        });
                });
            this.regions.removeRegions(regionsToRemove);

            // Add missing regions
            regionDescriptors.forEach((regionDescriptor: region.RegionDescriptor) => {
                var region = this.regions.getRegionByName(regionDescriptor.getName());
                if (!region) {
                    region = new api.content.page.region.RegionBuilder().
                        setName(regionDescriptor.getName()).
                        build();
                    this.regions.addRegion(region);
                }
            });
        }

        hasConfig(): boolean {
            return this.config != null;
        }

        getConfig(): api.data.RootDataSet {
            return this.config;
        }

        setConfig(value: api.data.RootDataSet) {
            this.config = value;
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