module api.content.page.region {

    import PropertyIdProvider = api.data.PropertyIdProvider;

    export class LayoutRegions extends api.content.page.AbstractRegions implements api.Equitable, api.Cloneable {

        constructor(builder: LayoutRegionsBuilder) {

            super(builder.regions);
        }

        mergeRegions(descriptorRegions: RegionDescriptor[], parent: LayoutComponent): LayoutRegions {
            return new LayoutRegionsMerger().merge(this, descriptorRegions, parent);
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, LayoutRegions)) {
                return false;
            }

            return super.equals(o);
        }

        clone(generateNewPropertyIds: boolean = false): LayoutRegions {
            return new LayoutRegionsBuilder(this, generateNewPropertyIds).build();
        }
    }

    export class LayoutRegionsBuilder {

        regions: Region[] = [];

        constructor(source?: LayoutRegions, generateNewPropertyIds: boolean = false) {
            if (source) {
                source.getRegions().forEach((region: Region) => {
                    this.regions.push(region.clone(generateNewPropertyIds));
                });
            }
        }

        fromJson(regionsJson: RegionJson[], layoutComponent: LayoutComponent,
                 propertyIdProvider: PropertyIdProvider): LayoutRegionsBuilder {

            regionsJson.forEach((regionJson: RegionJson) => {

                var region = new RegionBuilder().
                    setName(regionJson.name).
                    setParent(layoutComponent).
                    build();

                regionJson.components.forEach((componentJson: ComponentTypeWrapperJson) => {
                    var component = ComponentFactory.createFromJson(componentJson, region, propertyIdProvider);
                    region.addComponent(component);
                });

                this.addRegion(region);
            });
            return this;
        }

        addRegion(value: Region): LayoutRegionsBuilder {
            this.regions.push(value);
            return this;
        }

        public build(): LayoutRegions {
            return new LayoutRegions(this);
        }
    }
}