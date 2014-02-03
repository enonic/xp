module api.content.page {

    export class PageRegions extends AbstractRegions {

        constructor(builder: PageRegionsBuilder) {
            super(builder.regions);
        }
    }

    export class PageRegionsBuilder {

        regions: region.Region[] = [];

        fromJson(regionsJson: region.json.RegionJson[]): PageRegionsBuilder {

            regionsJson.forEach((regionJson: region.json.RegionJson) => {

                var regionPath = new RegionPath(null, regionJson.name);

                var regionBuilder = new region.RegionBuilder().
                    setName(regionJson.name).
                    setPath(regionPath);

                regionJson.components.forEach((componentJson: api.content.page.json.PageComponentTypeWrapperJson) => {
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