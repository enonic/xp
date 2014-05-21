module api.liveedit.layout {

    import ItemType = api.liveedit.ItemType;

    export class LayoutItemType extends ItemType {

        private static INSTANCE = new LayoutItemType();

        static get(): LayoutItemType {
            return LayoutItemType.INSTANCE;
        }

        constructor() {
            super("layout", <ItemTypeConfigJson>{
                cssSelector: '[data-live-edit-type=layout]',
                draggable: true,
                cursor: 'move',
                iconCls: 'live-edit-font-icon-layout',
                highlighterStyle: {
                    stroke: 'rgba(255, 165, 0, 1)',
                    strokeDasharray: '5 5',
                    fill: 'rgba(100, 12, 36, 0)'
                },
                contextMenuConfig: ['parent', 'remove', 'clear', 'duplicate']
            });
        }

        isPageComponentType(): boolean {
            return true
        }
    }

    LayoutItemType.get();
}