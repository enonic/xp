module api.liveedit {

    import PartView = api.liveedit.part.PartView;

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

        createView(parentPartView: PartView, data: any, element?: HTMLElement, dummy?: boolean): ContentView {
            return new ContentView(parentPartView, element);
        }
    }

    ContentItemType.get();
}