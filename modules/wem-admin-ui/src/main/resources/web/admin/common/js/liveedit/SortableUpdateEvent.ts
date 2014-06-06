module api.liveedit {

    import Event2 = api.event.Event2;
    import RegionPath = api.content.page.RegionPath;
    import PageComponent = api.content.page.PageComponent;

    export class SortableUpdateEvent extends Event2 {

        private pageComponentView: PageComponentView<PageComponent>;

        private regionView: RegionView;

        private precedingComponentView: PageComponentView<PageComponent>;

        constructor(pageComponentView: PageComponentView<PageComponent>, regionView: RegionView,
                    preceodingComponentView: PageComponentView<PageComponent>) {
            api.util.assert(pageComponentView.getType().isPageComponentType(), "Expected itemView to be of type PageComponent");

            super();

            this.pageComponentView = pageComponentView;
            this.regionView = regionView;

            this.precedingComponentView = preceodingComponentView;
        }

        getComponentView(): PageComponentView<PageComponent> {
            return this.pageComponentView;
        }

        getRegionView(): RegionView {
            return this.regionView;
        }

        getRegionPath(): RegionPath {
            return this.regionView.getRegionPath();
        }

        getPrecedingComponentView(): PageComponentView<PageComponent> {
            return this.precedingComponentView;
        }

        static on(handler: (event: SortableUpdateEvent) => void, contextWindow: Window = window) {
            Event2.bind(api.util.getFullName(this), handler, contextWindow);
        }

        static un(handler: (event: SortableUpdateEvent) => void, contextWindow: Window = window) {
            Event2.unbind(api.util.getFullName(this), handler, contextWindow);
        }
    }
}