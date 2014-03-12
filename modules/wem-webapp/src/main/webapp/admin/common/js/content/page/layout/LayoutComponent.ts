module api.content.page.layout {

    export class LayoutComponent extends api.content.page.PageComponent {

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

        public toJson(): api.content.page.json.PageComponentTypeWrapperJson {
            var json: json.LayoutComponentJson = <json.LayoutComponentJson>super.toPageComponentJson();
            json.regions = this.regions.toJson();

            return <api.content.page.json.PageComponentTypeWrapperJson> {
                LayoutComponent: json
            };
        }
    }

    export class LayoutComponentBuilder extends api.content.page.PageComponentBuilder<LayoutComponent> {

        regions: LayoutRegions;

        public fromJson(json: json.LayoutComponentJson, regionPath: RegionPath): LayoutComponentBuilder {

            if (json.descriptor) {
                this.setDescriptor(api.content.page.DescriptorKey.fromString(json.descriptor));
            }
            var componentName = new api.content.page.ComponentName(json.name);
            this.setName(componentName);
            this.setConfig(api.data.DataFactory.createRootDataSet(json.config));
            this.setRegion(regionPath);

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