module api.liveedit {

    import Event2 = api.event.Event2;
    import ContentId = api.content.ContentId;
    import ComponentPath = api.content.page.ComponentPath;

    export class ImageSetEvent extends Event2 {

        private id: ContentId;

        private path: ComponentPath;

        private componentPlaceholder: any;

        private imageName: string;

        private errorMessage: string;

        setImageId(id: ContentId):ImageSetEvent {
            this.id = id;
            return this;
        }

        setComponentPath(path: string): ImageSetEvent {
            this.path = ComponentPath.fromString(path);
            return this;
        }

        setComponentPlaceholder(placeholder: any): ImageSetEvent {
            this.componentPlaceholder = placeholder;
            return this;
        }

        setName(name: string): ImageSetEvent {
            this.imageName = name;
            return this;
        }

        setErrorMessage(message: string): ImageSetEvent {
            this.errorMessage = message;
            return this;
        }

        getImageId(): ContentId {
            return this.id;
        }

        getComponentPath(): ComponentPath {
            return this.path;
        }

        getComponentPlaceholder(): any {
            return this.componentPlaceholder;
        }

        getImageName(): string {
            return this.imageName;
        }

        getErrorMessage(): string {
            return this.errorMessage;
        }

        static on(handler: (event: ImageSetEvent) => void, contextWindow: Window = window) {
            Event2.bind(api.util.getFullName(this), handler, contextWindow);
        }

        static un(handler: (event: ImageSetEvent) => void, contextWindow: Window = window) {
            Event2.unbind(api.util.getFullName(this), handler, contextWindow);
        }
    }
}