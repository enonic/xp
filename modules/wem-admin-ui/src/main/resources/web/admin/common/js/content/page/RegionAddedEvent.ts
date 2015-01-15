module api.content.page {

    export class RegionAddedEvent extends RegionsChangedEvent {

        private regionPath: api.content.page.region.RegionPath;

        constructor(regionPath: api.content.page.region.RegionPath) {
            super();
            this.regionPath = regionPath;
        }

        getRegionPath(): api.content.page.region.RegionPath {
            return this.regionPath;
        }
    }
}