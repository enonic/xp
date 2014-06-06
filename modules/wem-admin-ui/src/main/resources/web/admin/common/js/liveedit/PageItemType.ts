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
                    stroke: '',
                    strokeDasharray: '',
                    fill: ''
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

        createView(parent: any, data: any, element?: HTMLElement, dummy?: boolean): PageView {
            return new PageView(element);
        }
    }

    PageItemType.get();
}