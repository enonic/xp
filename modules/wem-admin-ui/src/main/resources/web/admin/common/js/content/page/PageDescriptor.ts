module api.content.page {

    export class PageDescriptor extends Descriptor implements api.Cloneable {

        private regions: region.RegionDescriptor[];

        constructor(builder: PageDescriptorBuilder) {
            super(builder);
            this.regions = builder.regions;
        }

        public getRegions(): region.RegionDescriptor[] {
            return this.regions;
        }

        public clone(): PageDescriptor {
            return new PageDescriptorBuilder(this).build();
        }
    }

    export class PageDescriptorBuilder extends DescriptorBuilder {

        regions: region.RegionDescriptor[];

        constructor(source?: PageDescriptor) {
            this.regions = [];
            if (source) {
                super(source);
                this.regions = source.getRegions();
            }
        }

        public fromJson(json: api.content.page.PageDescriptorJson): PageDescriptorBuilder {

            this.setName(new DescriptorName(json.name));
            this.setDisplayName(json.displayName);
            this.setConfig(json.config != null ? api.form.Form.fromJson(json.config) : null);
            this.setKey(DescriptorKey.fromString(json.key));
            for (var i = 0; i < json.regions.length; i++) {
                var region = new api.content.page.region.RegionDescriptorBuilder().fromJson(json.regions[i]).build();
                this.regions.push(region);
            }

            return this;
        }

        public setKey(key: DescriptorKey): PageDescriptorBuilder {
            this.key = key;
            return this;
        }

        public setName(value: DescriptorName): PageDescriptorBuilder {
            this.name = value;
            return this;
        }

        public setDisplayName(value: string): PageDescriptorBuilder {
            this.displayName = value;
            return this;
        }

        public setConfig(value: api.form.Form): PageDescriptorBuilder {
            this.config = value;
            return this;
        }

        public addRegion(value: region.RegionDescriptor): PageDescriptorBuilder {
            this.regions.push(value);
            return this;
        }

        public build(): PageDescriptor {
            return new PageDescriptor(this);
        }
    }
}