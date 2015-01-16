module api.content.page.region {

    import PropertyIdProvider = api.data.PropertyIdProvider;
    import PropertyTree = api.data.PropertyTree;

    export class LayoutComponent extends DescriptorBasedComponent implements api.Equitable, api.Cloneable {

        public debug: boolean = false;

        private regions: Regions;

        constructor(builder: LayoutComponentBuilder) {
            super(builder);

            if (builder.regions) {
                this.regions = builder.regions;
                this.regions.getRegions().forEach((region: Region) => {
                    region.setParent(this);
                });
            }
            else {
                this.regions = Regions.create().build();
            }

            this.regions.onChanged(this.handleRegionsChanged.bind(this));
        }

        private handleRegionsChanged(event: RegionsChangedEvent) {
            if (this.debug) {
                console.debug("LayoutComponent[" + this.getPath().toString() + "].onChanged: ", event);
            }
            this.notifyPropertyValueChanged("regions");
        }

        public getComponent(path: ComponentPath): Component {
            return this.regions.getComponent(path);
        }

        public getRegions(): Regions {
            return this.regions;
        }

        public setRegions(value: Regions) {

            var oldValue = this.regions;
            this.regions.unChanged(this.handleRegionsChanged);

            this.regions = value;
            this.regions.onChanged(this.handleRegionsChanged.bind(this));

            if (!api.ObjectHelper.equals(oldValue, value)) {
                if (this.debug) {
                    console.debug("LayoutComponent[" + this.getPath().toString() + "].regions reassigned: ", event);
                }
                this.notifyPropertyChanged("regions");
            }
        }

        isEmpty(): boolean {
            return !this.hasDescriptor();
        }

        public toJson(): ComponentTypeWrapperJson {
            var json: LayoutComponentJson = <LayoutComponentJson>super.toComponentJson();
            json.regions = this.regions.toJson();

            return <ComponentTypeWrapperJson> {
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

    export class LayoutComponentBuilder extends DescriptorBasedComponentBuilder<LayoutComponent> {

        regions: Regions;

        constructor(source?: LayoutComponent, generateNewPropertyIds: boolean = false) {

            super(source, generateNewPropertyIds);

            if (source) {
                this.regions = source.getRegions().clone(generateNewPropertyIds);
            }
        }

        public fromJson(json: LayoutComponentJson, region: Region, propertyIdProvider: PropertyIdProvider): LayoutComponent {

            if (json.descriptor) {
                this.setDescriptor(api.content.page.DescriptorKey.fromString(json.descriptor));
            }
            var componentName = new ComponentName(json.name);
            this.setName(componentName);
            if (json.config) {
                this.setConfig(PropertyTree.fromJson(json.config, propertyIdProvider));
            }
            this.setParent(region);

            var layoutComponent = this.build();
            var layoutRegions = Regions.create().fromJson(json.regions, propertyIdProvider, layoutComponent).build();
            layoutComponent.setRegions(layoutRegions);
            return layoutComponent;
        }

        public setRegions(value: Regions): LayoutComponentBuilder {
            this.regions = value;
            return this;
        }

        public build(): LayoutComponent {
            return new LayoutComponent(this);
        }
    }
}