module api.liveedit {

    import Event2 = api.event.Event2;
    import ComponentPath = api.content.page.ComponentPath;
    import RegionPath = api.content.page.RegionPath;

    export class SortableUpdateEvent extends Event2 {

        private pageComponentView: PageComponentView;

        private regionView: RegionView;

        private componentPath: ComponentPath;

        private precedingComponent: ComponentPath;

        constructor(pageComponentView: PageComponentView, regionView: RegionView) {
            api.util.assert(pageComponentView.getType().isPageComponentType(), "Expected itemView to be of type PageComponent");

            super();

            this.pageComponentView = pageComponentView;
            this.regionView = regionView;
            this.componentPath = pageComponentView.getComponentPath();

            this.precedingComponent = pageComponentView.getPrecedingComponentPath();
        }

        getComponentView(): PageComponentView {
            return this.pageComponentView;
        }

        getRegionView(): RegionView {
            return this.regionView;
        }

        getComponentPath(): ComponentPath {
            return this.componentPath;
        }

        getRegionPath(): RegionPath {
            return this.regionView.getRegionPath();
        }

        getPrecedingComponent(): ComponentPath {
            return this.precedingComponent;
        }

        static on(handler: (event: SortableUpdateEvent) => void, contextWindow: Window = window) {
            Event2.bind(api.util.getFullName(this), handler, contextWindow);
        }

        static un(handler: (event: SortableUpdateEvent) => void, contextWindow: Window = window) {
            Event2.unbind(api.util.getFullName(this), handler, contextWindow);
        }
    }
}