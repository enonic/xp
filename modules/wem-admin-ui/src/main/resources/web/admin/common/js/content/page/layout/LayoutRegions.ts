module api.content.page.layout {

    export class LayoutRegions extends api.content.page.AbstractRegions implements api.Equitable, api.Cloneable {

        constructor(builder: LayoutRegionsBuilder) {

            super(builder.regions);
        }

        mergeRegions(descriptorRegions: api.content.page.region.RegionDescriptor[], parentComponentPath: ComponentPath): LayoutRegions {
            return new LayoutRegionsMerger().merge(this, descriptorRegions, parentComponentPath);
        }

        equals(o: api.Equitable): boolean {

            if (!(o instanceof LayoutRegions)) {
                return false;
            }

            return super.equals(o);
        }

        clone(): LayoutRegions {
            return new LayoutRegionsBuilder(this).build();
        }
    }

    export class LayoutRegionsBuilder {

        regions: api.content.page.region.Region[] = [];

        constructor(source?: LayoutRegions) {
            if (source) {
                source.getRegions().forEach((region: api.content.page.region.Region) => {
                    this.regions.push(region.clone());
                });
            }
        }

        fromJson(regionsJson: api.content.page.region.RegionJson[], layoutComponent: ComponentPath): LayoutRegionsBuilder {

            regionsJson.forEach((regionJson: api.content.page.region.RegionJson) => {

                var regionPath = new RegionPath(layoutComponent, regionJson.name);

                var regionBuilder = new api.content.page.region.RegionBuilder().
                    setName(regionJson.name).
                    setPath(regionPath);

                regionJson.components.forEach((componentJson: api.content.page.PageComponentTypeWrapperJson) => {
                    var pageComponent = api.content.page.PageComponentFactory.createFromJson(componentJson, regionPath);
                    regionBuilder.addComponent(pageComponent);
                });


                this.addRegion(regionBuilder.build());
            });
            return this;
        }

        addRegion(value: api.content.page.region.Region): LayoutRegionsBuilder {
            this.regions.push(value);
            return this;
        }

        public build(): LayoutRegions {
            return new LayoutRegions(this);
        }
    }
}