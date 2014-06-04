module api.liveedit {

    import ComponentPath = api.content.page.ComponentPath;
    import PageComponentView = api.liveedit.PageComponentView;
    import PageComponent = api.content.page.PageComponent;

    export class PageComponentSelectEvent extends api.event.Event2 {

        private pageItemView: PageComponentView<PageComponent>;

        constructor(itemView: PageComponentView<PageComponent>) {
            super();
            this.pageItemView = itemView;
        }

        getItemView(): PageComponentView<PageComponent> {
            return this.pageItemView;
        }

        static on(handler: (event: PageComponentSelectEvent) => void, contextWindow: Window = window) {
            api.event.Event2.bind(api.util.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: PageComponentSelectEvent) => void, contextWindow: Window = window) {
            api.event.Event2.unbind(api.util.getFullName(this), handler, contextWindow);
        }
    }
}