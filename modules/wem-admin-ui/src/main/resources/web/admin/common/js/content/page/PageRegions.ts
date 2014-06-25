module api.content.page {

    export class PageRegions extends AbstractRegions implements api.Equitable, api.Cloneable {

        constructor(builder: PageRegionsBuilder) {
            super(builder.regions);
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, PageRegions)) {
                return false;
            }

            return super.equals(o);
        }

        clone(): PageRegions {
            return new PageRegionsBuilder(this).build();
        }
    }

    export class PageRegionsBuilder {

        regions: region.Region[] = [];

        constructor(source?: PageRegions) {
            if (source) {
                source.getRegions().forEach((region: api.content.page.region.Region) => {
                    this.regions.push(region.clone());
                });
            }
        }

        fromJson(regionsJson: api.content.page.region.RegionJson[]): PageRegionsBuilder {

            regionsJson.forEach((regionJson: api.content.page.region.RegionJson) => {

                var region = new api.content.page.region.RegionBuilder().
                    setName(regionJson.name).
                    setParent(null).
                    build();

                regionJson.components.forEach((componentJson: api.content.page.PageComponentTypeWrapperJson) => {
                    var pageComponent = PageComponentFactory.createFromJson(componentJson, region);
                    region.addComponent(pageComponent);
                });

                this.addRegion(region);
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