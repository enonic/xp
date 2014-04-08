module app.wizard.page {

    import ComponentPath = api.content.page.ComponentPath;
    import ContentId = api.content.ContentId;

    export class SetImageComponentImageEvent {

        private path: ComponentPath;

        private image: ContentId;

        private componentPlaceholder: api.dom.Element;

        private imageName:string;

        constructor(path: ComponentPath, image: ContentId, componentPlaceholder: api.dom.Element, imageName:string) {
            this.path = path;
            this.image = image;
            this.componentPlaceholder = componentPlaceholder;
            this.imageName = imageName;
        }

        getPath(): ComponentPath {
            return this.path;
        }

        getImage(): ContentId {
            return this.image;
        }

        getComponentPlaceholder() : api.dom.Element {
            return this.componentPlaceholder;
        }

        getImageName() : string {
            return this.imageName;
        }
    }
}