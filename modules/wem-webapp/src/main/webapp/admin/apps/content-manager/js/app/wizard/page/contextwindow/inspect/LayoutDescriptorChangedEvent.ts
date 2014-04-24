module app.wizard.page.contextwindow.inspect {

    export class LayoutDescriptorChangedEvent {

        private componentPath: api.content.page.ComponentPath;
        private descriptor: api.content.page.layout.LayoutDescriptor;

        constructor(componentPath: api.content.page.ComponentPath, descriptor: api.content.page.layout.LayoutDescriptor) {
            this.componentPath = componentPath;
            this.descriptor = descriptor;
        }

        getComponentPath(): api.content.page.ComponentPath {
            return this.componentPath;
        }

        getDescriptor(): api.content.page.layout.LayoutDescriptor {
            return this.descriptor;
        }
    }
}
