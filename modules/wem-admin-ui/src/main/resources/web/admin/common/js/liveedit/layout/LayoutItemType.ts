module api.liveedit.layout {

    import ItemType = api.liveedit.ItemType;

    export class LayoutItemType extends ItemType {

        private static INSTANCE = new LayoutItemType();

        static get(): LayoutItemType {
            return LayoutItemType.INSTANCE;
        }

        constructor() {
            super("layout");
        }

        isPageComponentType(): boolean {
            return true
        }
    }

    LayoutItemType.get();
}