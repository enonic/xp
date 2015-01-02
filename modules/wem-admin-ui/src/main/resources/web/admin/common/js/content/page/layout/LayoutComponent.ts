module api.content.page.layout {

    import Region = api.content.page.region.Region;
    import PropertyIdProvider = api.data.PropertyIdProvider;
    import PropertyTree = api.data.PropertyTree;

    export class LayoutComponent extends api.content.page.DescriptorBasedComponent implements api.Equitable, api.Cloneable {

        private regions: LayoutRegions;

        constructor(builder: LayoutComponentBuilder) {
            super(builder);
            if (builder.regions) {
                this.regions = builder.regions;
                this.regions.getRegions().forEach((region: Region) => {
                    region.setParent(this);
                });
            } else {
                this.regions = new api.content.page.layout.LayoutRegionsBuilder().build();
            }
        }

        public getComponent(path: ComponentPath): api.content.page.Component {
            return this.regions.getComponent(path);
        }

        public getLayoutRegions(): LayoutRegions {
            return this.regions;
        }

        public setLayoutRegions(value: LayoutRegions) {
            this.regions = value;
        }

        public toJson(): api.content.page.ComponentTypeWrapperJson {
            var json: LayoutComponentJson = <LayoutComponentJson>super.toPageComponentJson();
            json.regions = this.regions.toJson();

            return <api.content.page.ComponentTypeWrapperJson> {
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

        clone(generateNewPropertyIds: boolean = false): LayoutComponent {
            return new LayoutComponentBuilder(this, generateNewPropertyIds).build();
        }
    }

    export class LayoutComponentBuilder extends api.content.page.DescriptorBasedPageComponentBuilder<LayoutComponent> {

        regions: LayoutRegions;

        constructor(source?: LayoutComponent, generateNewPropertyIds: boolean = false) {

            super(source, generateNewPropertyIds);

            if (source) {
                this.regions = source.getLayoutRegions().clone(generateNewPropertyIds);
            }
        }

        public fromJson(json: LayoutComponentJson, region: Region, propertyIdProvider: PropertyIdProvider): LayoutComponent {

            if (json.descriptor) {
                this.setDescriptor(api.content.page.DescriptorKey.fromString(json.descriptor));
            }
            var componentName = new api.content.page.ComponentName(json.name);
            this.setName(componentName);
            this.setConfig(PropertyTree.fromJson(json.config, propertyIdProvider));
            this.setParent(region);

            var layoutComponent = this.build();
            var layoutRegions = new layout.LayoutRegionsBuilder().fromJson(json.regions, layoutComponent, propertyIdProvider).build();
            layoutComponent.setLayoutRegions(layoutRegions);
            return layoutComponent;
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