module api.liveedit {

    import RegionPath = api.content.page.RegionPath;
    import RegionView = api.liveedit.RegionView;

    export class RegionSelectEvent extends api.event.Event2 {

        private pageItemView: RegionView;

        constructor(pageItemView: RegionView) {
            super();
            this.pageItemView = pageItemView;
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