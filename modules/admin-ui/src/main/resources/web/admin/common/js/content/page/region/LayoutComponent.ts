module api.content.page.region {

    import PropertyIdProvider = api.data.PropertyIdProvider;
    import PropertyTree = api.data.PropertyTree;

    export class LayoutComponent extends DescriptorBasedComponent implements api.Equitable, api.Cloneable {

        public debug: boolean = false;

        private regions: Regions;

        private componentPropertyChangedListeners: {(event: ComponentPropertyChangedEvent):void}[] = [];

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

            this.registerRegionsListeners(this.regions);
        }

        public getComponent(path: ComponentPath): Component {
            return this.regions.getComponent(path);
        }

        public getRegions(): Regions {
            return this.regions;
        }

        public setRegions(value: Regions) {

            var oldValue = this.regions;
            if (oldValue) {
                this.unregisterRegionsListeners(oldValue);
            }
            this.regions.unChanged(this.handleRegionsChanged);

            this.regions = value;
            this.registerRegionsListeners(this.regions);

            if (!api.ObjectHelper.equals(oldValue, value)) {
                if (this.debug) {
                    console.debug("LayoutComponent[" + this.getPath().toString() + "].regions reassigned: ", event);
                }
                this.notifyPropertyChanged("regions");
            }
        }

        setDescriptor(descriptorKey: DescriptorKey, descriptor?: LayoutDescriptor) {

            super.setDescriptor(descriptorKey, descriptor);
            this.addRegions(descriptor);
        }

        addRegions(layoutDescriptor: LayoutDescriptor) {
            var sourceRegions = this.getRegions();
            var mergedRegions = sourceRegions.mergeRegions(layoutDescriptor.getRegions(), this);
            this.setRegions(mergedRegions);
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

        private registerRegionsListeners(regions: api.content.page.region.Regions) {

            if (this.forwardComponentPropertyChangedEvent.bind) {
                regions.onChanged(this.handleRegionsChanged.bind(this));
                regions.onComponentPropertyChanged(this.forwardComponentPropertyChangedEvent.bind(this));
            }
            else {
                regions.onChanged((event) => {
                    this.handleRegionsChanged(event);
                });
                regions.onComponentPropertyChanged((event) => {
                    this.forwardComponentPropertyChangedEvent(event);
                });
            }
        }

        private unregisterRegionsListeners(regions: api.content.page.region.Regions) {

            regions.unChanged(this.handleRegionsChanged);
            regions.unComponentPropertyChanged(this.forwardComponentPropertyChangedEvent);
        }

        private handleRegionsChanged(event: RegionsChangedEvent) {
            if (this.debug) {
                console.debug("LayoutComponent[" + this.getPath().toString() + "].onChanged: ", event);
            }
            this.notifyPropertyValueChanged("regions");
        }

        onComponentPropertyChanged(listener: (event: ComponentPropertyChangedEvent)=>void) {
            this.componentPropertyChangedListeners.push(listener);
        }

        unComponentPropertyChanged(listener: (event: ComponentPropertyChangedEvent)=>void) {
            this.componentPropertyChangedListeners =
            this.componentPropertyChangedListeners.filter((curr: (event: ComponentPropertyChangedEvent)=>void) => {
                return listener != curr;
            });
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
            this.setName(json.name ? new ComponentName(json.name) : null);
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