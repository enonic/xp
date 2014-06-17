module api.content.page.layout {

    export class LayoutRegions extends api.content.page.AbstractRegions implements api.Equitable, api.Cloneable {

        constructor(builder: LayoutRegionsBuilder) {

            super(builder.regions);
        }

        mergeRegions(descriptorRegions: api.content.page.region.RegionDescriptor[], parent: LayoutComponent): LayoutRegions {
            return new LayoutRegionsMerger().merge(this, descriptorRegions, parent);
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, LayoutRegions)) {
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

        fromJson(regionsJson: api.content.page.region.RegionJson[], layoutComponent: LayoutComponent): LayoutRegionsBuilder {

            regionsJson.forEach((regionJson: api.content.page.region.RegionJson) => {

                var region = new api.content.page.region.RegionBuilder().
                    setName(regionJson.name).
                    setParent(layoutComponent).
                    build();

                regionJson.components.forEach((componentJson: api.content.page.PageComponentTypeWrapperJson) => {
                    var pageComponent = api.content.page.PageComponentFactory.createFromJson(componentJson, region);
                    region.addComponent(pageComponent);
                });

                this.addRegion(region);
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