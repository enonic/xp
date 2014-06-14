module api.liveedit {

    import PartComponentView = api.liveedit.part.PartComponentView;

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

        createView(config: CreateItemViewConfig<PartComponentView,any>): ContentView {
            return new ContentView(new ContentViewBuilder().
                setParentPartComponentView(config.parentView).
                setParentElement(config.parentElement).
                setElement(config.element));
        }
    }

    ContentItemType.get();
}