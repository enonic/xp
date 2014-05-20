module api.liveedit {

    import Event2 = api.event.Event2;
    import ComponentPath = api.content.page.ComponentPath;
    import Descriptor = api.content.page.Descriptor;
    import ItemView = api.liveedit.ItemView;

    export class PageComponentSetDescriptorEvent extends Event2 {

        private path: ComponentPath;

        private descriptor: Descriptor;

        private pageItemView: ItemView;

        constructor(path: ComponentPath, descriptor: Descriptor, itemView: ItemView) {
            super();
            this.path = path;
            this.descriptor = descriptor;
            this.pageItemView = itemView;
        }

        getPath(): ComponentPath {
            return this.path;
        }

        getDescriptor(): Descriptor {
            return this.descriptor;
        }

        getItemView(): ItemView {
            return this.pageItemView;
        }

        static on(handler: (event: PageComponentSetDescriptorEvent) => void, contextWindow: Window = window) {
            Event2.bind(api.util.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: PageComponentSetDescriptorEvent) => void, contextWindow: Window = window) {
            Event2.unbind(api.util.getFullName(this), handler, contextWindow);
        }
    }
}