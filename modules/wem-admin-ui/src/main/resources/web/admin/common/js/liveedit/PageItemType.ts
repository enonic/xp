module api.liveedit {

    import Content = api.content.Content;

    export class PageItemType extends ItemType {

        private static INSTANCE = new PageItemType();

        static get(): PageItemType {
            return PageItemType.INSTANCE;
        }

        private content: Content;

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

            ContentSetEvent.on((event: ContentSetEvent) => {
                this.content = event.getContent();
            });
        }

        getContent(): Content {
            return this.content;
        }

        createView(element: HTMLElement, dummy: boolean = true): PageView {
            return new PageView(element);
        }
    }

    PageItemType.get();
}