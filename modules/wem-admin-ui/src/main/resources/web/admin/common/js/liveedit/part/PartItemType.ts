module api.liveedit.part {

    import ItemType = api.liveedit.ItemType;

    export class PartItemType extends ItemType {

        private static INSTANCE = new PartItemType();

        static get(): PartItemType {
            return PartItemType.INSTANCE;
        }

        constructor() {
            super("part", <ItemTypeConfigJson>{
                cssSelector: '[data-live-edit-type=part]',
                draggable: true,
                cursor: 'move',
                iconCls: 'live-edit-font-icon-part',
                highlighterStyle: {
                    stroke: 'rgba(68, 68, 68, 1)',
                    strokeDasharray: '5 5',
                    fill: 'rgba(255, 255, 255, 0)'
                },
                contextMenuConfig: ['parent', 'remove', 'clear', 'duplicate']
            });
        }

        isPageComponentType(): boolean {
            return true
        }
    }

    PartItemType.get();
}