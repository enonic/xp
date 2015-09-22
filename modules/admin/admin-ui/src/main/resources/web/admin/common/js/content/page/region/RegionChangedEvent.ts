module api.content.page.region {

    export class RegionChangedEvent extends RegionsChangedEvent {

        private regionPath: RegionPath;

        constructor(regionPath: RegionPath) {
            super();
            this.regionPath = regionPath;
        }

        getRegionPath(): RegionPath {
            return this.regionPath;
        }
    }
}