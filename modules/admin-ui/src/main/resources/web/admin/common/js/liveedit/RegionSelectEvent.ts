module api.liveedit {

    import RegionPath = api.content.page.region.RegionPath;
    import RegionView = api.liveedit.RegionView;

    export class RegionSelectEvent extends api.event.Event {

        private pageItemView: RegionView;

        constructor(regionView: RegionView) {
            super();
            this.pageItemView = regionView;
        }

        getRegionView(): RegionView {
            return this.pageItemView;
        }

        static on(handler: (event: RegionSelectEvent) => void, contextWindow: Window = window) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }

        static un(handler: (event: RegionSelectEvent) => void, contextWindow: Window = window) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }
    }
}