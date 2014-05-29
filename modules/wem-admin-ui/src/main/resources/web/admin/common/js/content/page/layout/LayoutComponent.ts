module api.content.page.layout {

    export class LayoutComponent extends api.content.page.DescriptorBasedPageComponent implements api.Equitable, api.Cloneable {

        private regions: LayoutRegions;

        constructor(builder: LayoutComponentBuilder) {
            super(builder);
            if (builder.regions) {
                this.regions = builder.regions;
            } else {
                this.regions = new api.content.page.layout.LayoutRegionsBuilder().build();
            }
        }

        public getComponent(path: ComponentPath): api.content.page.PageComponent {
            return this.regions.getComponent(path);
        }

        public getLayoutRegions(): LayoutRegions {
            return this.regions;
        }

        public setLayoutRegions(value: LayoutRegions) {
            this.regions = value;
        }

        setName(name: api.content.page.ComponentName) {
            super.setName(name);

            this.regions.setParent(this.getPath());
        }

        public toJson(): api.content.page.PageComponentTypeWrapperJson {
            var json: LayoutComponentJson = <LayoutComponentJson>super.toPageComponentJson();
            json.regions = this.regions.toJson();

            return <api.content.page.PageComponentTypeWrapperJson> {
                LayoutComponent: json
            };
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, LayoutComponent)) {
                return false;
            }

            var other = <LayoutComponent>o;

            if (!super.equals(o)) {
                return false;
            }

            if (!api.ObjectHelper.equals(this.regions, other.regions)) {
                return false;
            }

            return true;
        }

        clone(): LayoutComponent {
            return new LayoutComponentBuilder(this).build();
        }
    }

    export class LayoutComponentBuilder extends api.content.page.DescriptorBasedPageComponentBuilder<LayoutComponent> {

        regions: LayoutRegions;

        constructor(source?: LayoutComponent) {

            super(source);

            if (source) {
                this.regions = source.getLayoutRegions().clone();
            }
        }

        public fromJson(json: LayoutComponentJson, regionPath: RegionPath): LayoutComponentBuilder {

            if (json.descriptor) {
                this.setDescriptor(api.content.page.DescriptorKey.fromString(json.descriptor));
            }
            var componentName = new api.content.page.ComponentName(json.name);
            this.setName(componentName);
            this.setConfig(api.data.DataFactory.createRootDataSet(json.config));
            this.setParent(regionPath);

            var componentPath = ComponentPath.fromRegionPathAndComponentName(regionPath, componentName);

            this.setRegions(json.regions != null ? new LayoutRegionsBuilder().fromJson(json.regions, componentPath).build() : null);
            return this;
        }

        public setRegions(value: LayoutRegions): LayoutComponentBuilder {
            this.regions = value;
            return this;
        }

        public build(): LayoutComponent {
            return new LayoutComponent(this);
        }
    }
}