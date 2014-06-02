module api.liveedit {

    export class RegionItemType extends ItemType {

        private static INSTANCE = new RegionItemType();

        static get(): RegionItemType {
            return RegionItemType.INSTANCE;
        }

        constructor() {
            super("region", <ItemTypeConfigJson>{
                cssSelector: '[data-live-edit-type=region]',
                draggable: false,
                cursor: 'pointer',
                iconCls: 'live-edit-font-icon-region',
                highlighterStyle: {
                    stroke: 'rgba(20, 20, 20, 1)',
                    strokeDasharray: '',
                    fill: 'rgba(255, 255, 255, 0)'
                },
                contextMenuConfig: ['parent', 'clearRegion']

            });
        }

        createView(element?: HTMLElement, dummy?: boolean): RegionView {
            return new RegionView(element);
        }
    }

    RegionItemType.get();
}