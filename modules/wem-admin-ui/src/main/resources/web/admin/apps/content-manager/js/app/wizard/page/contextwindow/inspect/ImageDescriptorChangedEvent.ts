module app.wizard.page.contextwindow.inspect {

    import ImageComponentView = api.liveedit.image.ImageComponentView;

    export class ImageDescriptorChangedEvent {

        private imageView: ImageComponentView;

        private descriptor: api.content.page.image.ImageDescriptor;

        constructor(imageView: ImageComponentView, descriptor: api.content.page.image.ImageDescriptor) {
            this.imageView = imageView;
            this.descriptor = descriptor;
        }

        getImageComponentView(): ImageComponentView {
            return this.imageView;
        }

        getDescriptor(): api.content.page.image.ImageDescriptor {
            return this.descriptor;
        }
    }
}