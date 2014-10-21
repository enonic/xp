module api.liveedit.image {

    import Event = api.event.Event;
    import ContentId = api.content.ContentId;

    export class ImageComponentSetImageEvent extends Event {

        private id: ContentId;

        private imageView: ImageComponentView;

        private imageName: string;

        private errorMessage: string;

        setImageId(id: ContentId): ImageComponentSetImageEvent {
            this.id = id;
            return this;
        }

        setImageComponentView(value: ImageComponentView): ImageComponentSetImageEvent {
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

        getImageComponentView(): ImageComponentView {
            return this.imageView;
        }

        getImageName(): string {
            return this.imageName;
        }

        getErrorMessage(): string {
            return this.errorMessage;
        }

        static on(handler: (event: ImageComponentSetImageEvent) => void, contextWindow: Window = window) {
            Event.bind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }

        static un(handler: (event: ImageComponentSetImageEvent) => void, contextWindow: Window = window) {
            Event.unbind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }
    }
}