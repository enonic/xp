module api.liveedit {

    import Content = api.content.Content;
    import SiteTemplate = api.content.site.template.SiteTemplate;

    export class PageItemType extends ItemType {

        private static INSTANCE = new PageItemType();

        static get(): PageItemType {
            return PageItemType.INSTANCE;
        }

        private content: Content;

        private siteTemplate: SiteTemplate;

        constructor() {
            super("page", <ItemTypeConfigJson>{
                cssSelector: '[data-live-edit-type=page]',
                draggable: false,
                cursor: 'pointer',
                iconCls: 'live-edit-font-icon-page',
                highlighterStyle: {
                    stroke: 'rgba(20, 20, 20, 1)',
                    strokeDasharray: '',
                    fill: 'rgba(255, 255, 255, 0)'
                },
                contextMenuConfig: ['reset']
            });
        }

        setContent(content: Content) {
            this.content = content;
        }

        setSiteTemplate(siteTemplate: SiteTemplate) {
            this.siteTemplate = siteTemplate;
        }

        getContent(): Content {
            return this.content;
        }

        getSiteTemplate(): SiteTemplate {
            return this.siteTemplate;
        }

        createView(config: CreateItemViewConfig<any,any>): PageView {
            throw new Error("Not supported");
        }
    }

    PageItemType.get();
}