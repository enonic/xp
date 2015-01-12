module api.content.page.region {

    export class ImageComponentType extends ComponentType {

        constructor() {
            super("image");
        }

        newComponentBuilder(): ImageComponentBuilder {
            return new ImageComponentBuilder();
        }
    }

    new ImageComponentType();

}