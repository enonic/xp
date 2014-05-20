module api.liveedit.part {

    import ItemType = api.liveedit.ItemType;

    export class PartItemType extends ItemType {

        private static INSTANCE = new PartItemType();

        static get(): PartItemType {
            return PartItemType.INSTANCE;
        }

        constructor() {
            super("part");
        }

        isPageComponentType(): boolean {
            return true
        }
    }

    PartItemType.get();
}