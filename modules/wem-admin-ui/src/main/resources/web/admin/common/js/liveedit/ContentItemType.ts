module api.liveedit {

    export class ContentItemType extends ItemType {

        private static INSTANCE = new ContentItemType();

        static get(): ContentItemType {
            return ContentItemType.INSTANCE;
        }

        constructor() {
            super("content", <ItemTypeConfigJson>{
                cssSelector: '[data-live-edit-type=content]',
                draggable: false,
                cursor: 'pointer',
                iconCls: 'live-edit-font-icon-content',
                highlighterStyle: {
                    stroke: '',
                    strokeDasharray: '',
                    fill: 'rgba(0, 108, 255, .25)'
                },
                contextMenuConfig: ['parent', 'opencontent', 'view']
            });
        }

        createView(element: HTMLElement, dummy: boolean = true): ItemView {
            return new ItemView(this, element, dummy);
        }
    }

    ContentItemType.get();
}