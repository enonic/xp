module api.liveedit {

    import Event2 = api.event.Event2;
    import RegionPath = api.content.page.RegionPath;

    export class RegionEmptyEvent extends Event2 {

        private path: RegionPath;

        constructor(path: RegionPath) {
            super();
            this.path = path;
        }

        getPath(): RegionPath {
            return this.path;
        }

        static on(handler: (event: RegionEmptyEvent) => void, contextWindow: Window = window) {
            Event2.bind(api.util.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: RegionEmptyEvent) => void, contextWindow: Window = window) {
            Event2.unbind(api.util.getFullName(this), handler, contextWindow);
        }
    }
}