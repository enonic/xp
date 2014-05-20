module api.liveedit.image {

    import ItemType = api.liveedit.ItemType;

    export class ImageItemType extends ItemType {

        private static INSTANCE = new ImageItemType();

        static get(): ImageItemType {
            return ImageItemType.INSTANCE;
        }

        constructor() {
            super("image");
        }

        isPageComponentType(): boolean {
            return true
        }
    }

    ImageItemType.get();
}