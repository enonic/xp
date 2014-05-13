module api.content.page.image {

    import PageComponentType = api.content.page.PageComponentType;

    export class ImageComponentType extends PageComponentType {

        constructor() {
            super("image");
        }

        newComponentBuilder(): ImageComponentBuilder {
            return new ImageComponentBuilder();
        }
    }

    new ImageComponentType();

}