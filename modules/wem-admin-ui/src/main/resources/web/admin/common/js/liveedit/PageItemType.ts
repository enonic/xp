module api.liveedit {

    export class PageItemType extends ItemType {

        private static INSTANCE = new PageItemType();

        static get(): PageItemType {
            return PageItemType.INSTANCE;
        }

        constructor() {
            super("page");
        }
    }

    PageItemType.get();
}