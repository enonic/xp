module api.content.page.layout {

    export class LayoutRegions extends api.content.page.AbstractRegions {

        constructor(builder: LayoutRegionsBuilder) {

            super(builder.regions);
        }
    }

    export class LayoutRegionsBuilder {

        regions: api.content.page.region.Region[] = [];

        fromJson(regionsJson: api.content.page.region.json.RegionJson[]): LayoutRegionsBuilder {

            regionsJson.forEach((regionJson: api.content.page.region.json.RegionJson) => {

                var regionBuilder = new api.content.page.region.RegionBuilder().
                    setName(regionJson.name);

                regionJson.components.forEach((componentJson: api.content.page.json.PageComponentTypeWrapperJson) => {
                    var pageComponent = api.content.page.PageComponentFactory.createFromJson(componentJson);
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