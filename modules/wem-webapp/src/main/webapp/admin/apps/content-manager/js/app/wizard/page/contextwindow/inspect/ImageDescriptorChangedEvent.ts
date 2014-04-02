module app.wizard.page.contextwindow.inspect {

    export class ImageDescriptorChangedEvent {

        private componentPath: api.content.page.ComponentPath;
        private descriptor: api.content.page.image.ImageDescriptor;

        constructor(componentPath: api.content.page.ComponentPath, descriptor: api.content.page.image.ImageDescriptor) {
            this.componentPath = componentPath;
            this.descriptor = descriptor;
        }

        getComponentPath(): api.content.page.ComponentPath {
            return this.componentPath;
        }

        getDescriptor(): api.content.page.image.ImageDescriptor {
            return this.descriptor;
        }
    }
}