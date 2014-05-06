module app.wizard.page {

    import RegionPath = api.content.page.RegionPath;

    export class RegionSelectedEvent {

        private path: RegionPath;

        constructor(path: RegionPath) {
            this.path = path;
        }

        getPath(): RegionPath {
            return this.path;
        }
    }
}