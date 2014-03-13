module api.content.page.layout {

    export class LayoutRegions extends api.content.page.AbstractRegions {

        constructor(builder: LayoutRegionsBuilder) {

            super(builder.regions);
        }
    }

    export class LayoutRegionsBuilder {

        regions: api.content.page.region.Region[] = [];

        fromJson(regionsJson: api.content.page.region.RegionJson[], layoutComponent:ComponentPath): LayoutRegionsBuilder {

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