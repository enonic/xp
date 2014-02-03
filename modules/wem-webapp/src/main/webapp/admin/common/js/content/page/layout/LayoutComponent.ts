module api.content.page.layout {

    export class LayoutComponent extends api.content.page.PageComponent {

        private regions: LayoutRegions;

        constructor(builder: LayoutComponentBuilder) {
            super(builder);
            this.regions = builder.regions;
        }

        public getComponent(path: ComponentPath): api.content.page.PageComponent {
            return this.regions.getComponent(path);
        }

        getLayoutRegions(): LayoutRegions {
            return this.regions;
        }

        toJson(): api.content.page.json.PageComponentTypeWrapperJson {
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
            if (json.template) {
                this.setTemplate(TemplateKey.fromString(json.template));
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