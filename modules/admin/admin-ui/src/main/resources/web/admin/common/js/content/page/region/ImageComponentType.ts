module api.content.page.region {

    export class ImageComponentType extends ComponentType {

        private static INSTANCE: ImageComponentType = new ImageComponentType();

        constructor() {
            super("image");
        }

        newComponentBuilder(): ImageComponentBuilder {
            return new ImageComponentBuilder();
        }

        public static get(): ImageComponentType {
            return ImageComponentType.INSTANCE;
        }
    }

}
