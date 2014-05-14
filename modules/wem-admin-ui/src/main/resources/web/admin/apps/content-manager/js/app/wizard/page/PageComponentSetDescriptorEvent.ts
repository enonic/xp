module app.wizard.page {

    import ComponentPath = api.content.page.ComponentPath;
    import Descriptor = api.content.page.Descriptor;

    export class PageComponentSetDescriptorEvent {

        private path: ComponentPath;

        private descriptor: Descriptor;

        private componentView: api.dom.Element;

        constructor(path: ComponentPath, descriptor: Descriptor, componentView: api.dom.Element) {
            this.path = path;
            this.descriptor = descriptor;
            this.componentView = componentView;
        }

        getPath(): ComponentPath {
            return this.path;
        }

        getDescriptor(): Descriptor {
            return this.descriptor;
        }

        getComponentView(): api.dom.Element {
            return this.componentView;
        }
    }
}