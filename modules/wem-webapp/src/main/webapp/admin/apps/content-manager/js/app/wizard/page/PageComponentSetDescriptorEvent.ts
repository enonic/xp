module app.wizard.page {

    import ComponentPath = api.content.page.ComponentPath;
    import Descriptor = api.content.page.Descriptor;

    export class PageComponentSetDescriptorEvent {

        private path: ComponentPath;

        private descriptor: Descriptor;

        private componentPlaceholder: api.dom.Element;

        constructor(path: ComponentPath, descriptor: Descriptor, componentPlaceholder: api.dom.Element) {
            this.path = path;
            this.descriptor = descriptor;
            this.componentPlaceholder = componentPlaceholder;
        }

        getPath(): ComponentPath {
            return this.path;
        }

        getDescriptor(): Descriptor {
            return this.descriptor;
        }

        getComponentPlaceholder() : api.dom.Element {
            return this.componentPlaceholder;
        }
    }
}