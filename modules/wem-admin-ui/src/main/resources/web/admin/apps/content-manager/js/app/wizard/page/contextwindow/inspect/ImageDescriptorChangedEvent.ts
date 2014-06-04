module app.wizard.page.contextwindow.inspect {

    import ImageView = api.liveedit.image.ImageView;

    export class ImageDescriptorChangedEvent {

        private imageView: ImageView;

        private descriptor: api.content.page.image.ImageDescriptor;

        constructor(imageView: ImageView, descriptor: api.content.page.image.ImageDescriptor) {
            this.imageView = imageView;
            this.descriptor = descriptor;
        }

        getImageView(): ImageView {
            return this.imageView;
        }

        getDescriptor(): api.content.page.image.ImageDescriptor {
            return this.descriptor;
        }
    }
}