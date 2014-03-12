module api.content.page.layout {

    export class LayoutDescriptor extends api.content.page.Descriptor {
        private regions: region.RegionDescriptor[];

        constructor(builder: LayoutDescriptorBuilder) {
            super(builder);
            this.regions = builder.regions;
        }

        public getRegions(): region.RegionDescriptor[] {
            return this.regions;
        }
    }

    export class LayoutDescriptorBuilder extends api.content.page.DescriptorBuilder {

        regions: region.RegionDescriptor[] = [];

        public fromJson(json: api.content.page.layout.json.LayoutDescriptorJson): LayoutDescriptorBuilder {

            this.setKey(api.content.page.DescriptorKey.fromString(json.key))
            this.setName(new api.content.page.DescriptorName(json.name));
            this.setDisplayName(json.displayName);
            this.setConfig(json.config != null ? new api.form.Form(json.config) : null);
            for (var i = 0; i < json.regions.length; i++) {
                var region = new api.content.page.region.RegionDescriptorBuilder().fromJson(json.regions[i]).build();
                this.regions.push(region);
            }

            return this;
        }

        public setKey(value: api.content.page.DescriptorKey): LayoutDescriptorBuilder {
            this.key = value;
            return this;
        }

        public setName(value: api.content.page.DescriptorName): LayoutDescriptorBuilder {
            this.name = value;
            return this;
        }

        public setDisplayName(value: string): LayoutDescriptorBuilder {
            this.displayName = value;
            return this;
        }

        public setConfig(value: api.form.Form): LayoutDescriptorBuilder {
            this.config = value;
            return this;
        }

        public addRegion(value: region.RegionDescriptor): LayoutDescriptorBuilder {
            this.regions.push(value);
            return this;
        }

        public build(): LayoutDescriptor {
            return new LayoutDescriptor(this);
        }
    }
}