module api.content.page {

    export class PageRegions extends AbstractRegions implements api.Equitable {

        constructor(builder: PageRegionsBuilder) {
            super(builder.regions);
        }

        equals(o: api.Equitable): boolean {

            if (!(o instanceof PageRegions)) {
                return false;
            }

            return super.equals(o);
        }
    }

    export class PageRegionsBuilder {

        regions: region.Region[] = [];

        fromJson(regionsJson: api.content.page.region.RegionJson[]): PageRegionsBuilder {

            regionsJson.forEach((regionJson: api.content.page.region.RegionJson) => {

                var regionPath = new RegionPath(null, regionJson.name);

                var regionBuilder = new region.RegionBuilder().
                    setName(regionJson.name).
                    setPath(regionPath);

                regionJson.components.forEach((componentJson: api.content.page.PageComponentTypeWrapperJson) => {
                    var pageComponent = PageComponentFactory.createFromJson(componentJson, regionPath);
                    regionBuilder.addComponent(pageComponent);
                });


                this.addRegion(regionBuilder.build());
            });
            return this;
        }

        addRegion(value: region.Region): PageRegionsBuilder {
            this.regions.push(value);
            return this;
        }

        public build(): PageRegions {
            return new PageRegions(this);
        }
    }
}