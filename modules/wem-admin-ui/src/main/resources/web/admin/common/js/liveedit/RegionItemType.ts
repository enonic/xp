module api.liveedit {

    export class RegionItemType extends ItemType {

        private static INSTANCE = new RegionItemType();

        static get(): RegionItemType {
            return RegionItemType.INSTANCE;
        }

        constructor() {
            super("region");
        }
    }

    RegionItemType.get();
}