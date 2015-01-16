module api.content.page.region {

    export class BaseRegionChangedEvent {

        private path: RegionPath;

        constructor(path: RegionPath) {
            this.path = path;
        }

        public getPath(): RegionPath {
            return this.path;
        }
    }
}