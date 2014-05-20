module api.liveedit {

    import Event2 = api.event.Event2;
    import ComponentPath = api.content.page.ComponentPath;
    import RegionPath = api.content.page.RegionPath;

    export class SortableUpdateEvent extends Event2 {

        private itemView: ItemView;

        private componentPath: ComponentPath;

        private region: RegionPath;

        private precedingComponent: ComponentPath;

        constructor(itemView: ItemView) {
            api.util.assert(itemView.getType().isPageComponentType(), "Expected itemView to be of type PageComponent");

            super();

            this.itemView = itemView;
            this.componentPath = itemView.getComponentPath();

            var region = itemView.getParentRegion();
            this.region = region.getRegionPath();
            this.precedingComponent = itemView.getPrecedingComponentPath();
        }

        getComponentView(): ItemView {
            return this.itemView;
        }

        getComponentPath(): ComponentPath {
            return this.componentPath;
        }

        getRegion(): RegionPath {
            return this.region;
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