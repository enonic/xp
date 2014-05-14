module app.wizard.page {

    import ComponentPath = api.content.page.ComponentPath;
    import ContentId = api.content.ContentId;

    export class ImageComponentSetImageEvent {

        private path: ComponentPath;

        private image: ContentId;

        private componentView: api.dom.Element;

        private imageName: string;

        constructor(path: ComponentPath, image: ContentId, componentView: api.dom.Element, imageName: string) {
            this.path = path;
            this.image = image;
            this.componentView = componentView;
            this.imageName = imageName;
        }

        getPath(): ComponentPath {
            return this.path;
        }

        getImage(): ContentId {
            return this.image;
        }

        getComponentView(): api.dom.Element {
            return this.componentView;
        }

        getImageName(): string {
            return this.imageName;
        }
    }
}