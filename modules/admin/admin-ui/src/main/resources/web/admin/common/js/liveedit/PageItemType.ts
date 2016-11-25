module api.liveedit {

    import Content = api.content.Content;
    import Site = api.content.site.Site;

    export class PageItemType extends ItemType {

        private static INSTANCE = new PageItemType();

        static get(): PageItemType {
            return PageItemType.INSTANCE;
        }

        constructor() {
            super("page");
        }

        protected getItemTypeConfig(itemType: string): ItemTypeConfig {
            return new ItemTypeConfig(<ItemTypeConfigJson>{
                cssSelector: '[data-portal-component-type=page]',
                draggable: false,
                cursor: 'pointer',
                iconCls: api.StyleHelper.COMMON_PREFIX + 'icon-page',
                highlighterStyle: {
                    stroke: 'rgba(20, 20, 20, 1)', // not used
                    strokeDasharray: '7 7',
                    fill: 'rgba(255, 255, 255, 0)' // not used
                },
                contextMenuConfig: ['reset']
            });
        }

        createView(config: CreateItemViewConfig<any,any>): PageView {
            throw new Error("Not supported");
        }
    }

    PageItemType.get();
}