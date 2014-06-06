module api.liveedit.image {

    import Event2 = api.event.Event2;
    import ContentId = api.content.ContentId;

    export class ImageComponentSetImageEvent extends Event2 {

        private id: ContentId;

        private imageView: ImageView;

        private imageName: string;

        private errorMessage: string;

        setImageId(id: ContentId): ImageComponentSetImageEvent {
            this.id = id;
            return this;
        }

        setImageView(value: ImageView): ImageComponentSetImageEvent {
            this.imageView = value;
            return this;
        }

        setName(name: string): ImageComponentSetImageEvent {
            this.imageName = name;
            return this;
        }

        setErrorMessage(message: string): ImageComponentSetImageEvent {
            this.errorMessage = message;
            return this;
        }

        getImageId(): ContentId {
            return this.id;
        }

        getImageView(): ImageView {
            return this.imageView;
        }

        getImageName(): string {
            return this.imageName;
        }

        getErrorMessage(): string {
            return this.errorMessage;
        }

        static on(handler: (event: ImageComponentSetImageEvent) => void, contextWindow: Window = window) {
            Event2.bind(api.util.getFullName(this), handler, contextWindow);
        }

        static un(handler: (event: ImageComponentSetImageEvent) => void, contextWindow: Window = window) {
            Event2.unbind(api.util.getFullName(this), handler, contextWindow);
        }
    }
}