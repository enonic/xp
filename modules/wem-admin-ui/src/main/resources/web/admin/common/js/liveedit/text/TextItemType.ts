module api.liveedit.text {

    import ItemType = api.liveedit.ItemType;

    export class TextItemType extends ItemType {

        private static INSTANCE = new TextItemType();

        static get(): TextItemType {
            return TextItemType.INSTANCE;
        }

        constructor() {
            super("text");
        }

        isPageComponentType(): boolean {
            return true
        }
    }

    TextItemType.get();
}