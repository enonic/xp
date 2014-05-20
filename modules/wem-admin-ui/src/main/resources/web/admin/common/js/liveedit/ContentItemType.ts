module api.liveedit {

    export class ContentItemType extends ItemType {

        private static INSTANCE = new ContentItemType();

        static get(): ContentItemType {
            return ContentItemType.INSTANCE;
        }

        constructor() {
            super("content");
        }
    }

    ContentItemType.get();
}