module app.wizard.page {

    import ComponentPath = api.content.page.ComponentPath;
    import Descriptor = api.content.page.Descriptor;
    import ItemView = api.liveedit.ItemView;

    export class PageComponentSetDescriptorEvent {

        private path: ComponentPath;

        private descriptor: Descriptor;

        private componentView: ItemView;

        constructor(path: ComponentPath, descriptor: Descriptor, itemView: ItemView) {
            this.path = path;
            this.descriptor = descriptor;
            this.componentView = itemView;
        }

        getPath(): ComponentPath {
            return this.path;
        }

        getDescriptor(): Descriptor {
            return this.descriptor;
        }

        getComponentView(): ItemView {
            return this.componentView;
        }
    }
}