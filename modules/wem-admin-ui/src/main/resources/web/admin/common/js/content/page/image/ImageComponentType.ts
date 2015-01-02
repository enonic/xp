module api.content.page.image {

    import ComponentType = api.content.page.ComponentType;

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