module api.content.page.region {

    export class PageRegions {

        private regionByName: {[s:string] : Region;} = {};

        constructor(builder: PageRegionsBuilder) {

            builder.regions.forEach((region: Region) => {
                if (this.regionByName[region.getName()] != undefined) {
                    throw new Error("Regions in a Page must be unique by name, duplicate found: " + region.getName());
                }

                this.regionByName[region.getName()] = region;
            });
        }

        getRegion(name: string): Region {
            return this.regionByName[name];
        }
    }

    export class PageRegionsBuilder {

        regions: Region[] = [];

        addRegion(value: Region): PageRegionsBuilder {
            this.regions.push(value);
            return this;
        }

        public build(): PageRegions {
            return new PageRegions(this);
        }
    }
}