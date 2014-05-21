module api.liveedit {

    export class PageItemType extends ItemType {

        private static INSTANCE = new PageItemType();

        static get(): PageItemType {
            return PageItemType.INSTANCE;
        }

        constructor() {
            super("page", <ItemTypeConfigJson>{
                cssSelector: '[data-live-edit-type=page]',
                draggable: false,
                cursor: 'pointer',
                iconCls: 'live-edit-font-icon-page',
                highlighterStyle: {
                    stroke: '',
                    strokeDasharray: '',
                    fill: ''
                },
                contextMenuConfig: ['reset']
            });
        }
    }

    PageItemType.get();
}