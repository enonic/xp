module app.wizard.page.contextwindow.inspect {

    export class ImageDescriptorChangedEvent {

        private imageView: api.liveedit.image.ImageView;
        private descriptor: api.content.page.image.ImageDescriptor;

        constructor(imageView: api.liveedit.image.ImageView, descriptor: api.content.page.image.ImageDescriptor) {
            this.imageView = imageView;
            this.descriptor = descriptor;
        }

        getImageView(): api.liveedit.image.ImageView {
            return this.imageView;
        }

        getDescriptor(): api.content.page.image.ImageDescriptor {
            return this.descriptor;
        }
    }
}