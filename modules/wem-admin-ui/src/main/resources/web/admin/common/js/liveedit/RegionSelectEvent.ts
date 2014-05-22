module api.liveedit {

    import RegionPath = api.content.page.RegionPath;
    import RegionView = api.liveedit.RegionView;

    export class RegionSelectEvent extends api.event.Event2 {

        private regionPath: RegionPath;

        private pageItemView: RegionView;

        constructor(regionPath: api.content.page.RegionPath, pageItemView: RegionView) {
            super();
            this.regionPath = regionPath;
            this.pageItemView = pageItemView;
        }

        getPath(): RegionPath {
            return this.regionPath;
        }

        getRegionView(): RegionView {
            return this.pageItemView;
        }

        static on(handler: (event: RegionSelectEvent) => void, contextWindow: Window = window) {
            api.event.Event2.bind(api.util.getFullName(this), handler, contextWindow);
        }

        static un(handler: (event: RegionSelectEvent) => void, contextWindow: Window = window) {
            api.event.Event2.unbind(api.util.getFullName(this), handler, contextWindow);
        }
    }
}