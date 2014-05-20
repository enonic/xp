module api.liveedit {

    import Event2 = api.event.Event2;
    import RegionPath = api.content.page.RegionPath;
    import ComponentPath = api.content.page.ComponentPath;
    import PageComponentType = api.content.page.PageComponentType;

    export class PageComponentDuplicateEvent extends Event2 {

        private originItemView: ItemView;

        private duplicationItemView: ItemView;

        constructor(originItemView: ItemView, duplicationItemView: ItemView) {
            super();
            this.originItemView = originItemView;
            this.duplicationItemView = duplicationItemView;
        }

        getItemView(): ItemView {
            return this.duplicationItemView;
        }

        getPath(): ComponentPath {
            return this.originItemView.getComponentPath();
        }

        getType(): PageComponentType {
            return this.originItemView.getType().toPageComponentType();
        }

        getRegion(): RegionPath {
            return this.originItemView.getParentRegion().getRegionPath();
        }

        static on(handler: (event: PageComponentDuplicateEvent) => void, contextWindow: Window = window) {
            Event2.bind(api.util.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: PageComponentDuplicateEvent) => void, contextWindow: Window = window) {
            Event2.unbind(api.util.getFullName(this), handler, contextWindow);
        }
    }
}