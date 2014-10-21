module api.liveedit {

    import PageComponentType = api.content.page.PageComponentType;
    import RegionPath = api.content.page.RegionPath;
    import PageComponent = api.content.page.PageComponent;

    export class PageComponentAddedEvent extends api.event.Event {

        private pageComponentView: PageComponentView<PageComponent>;

        setPageComponentView(pageComponentView: PageComponentView<PageComponent>): PageComponentAddedEvent {
            this.pageComponentView = pageComponentView;
            return this;
        }

        getPageComponentView(): PageComponentView<PageComponent> {
            return this.pageComponentView;
        }

        static on(handler: (event: PageComponentAddedEvent) => void, contextWindow: Window = window) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: PageComponentAddedEvent) => void, contextWindow: Window = window) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }
    }
}