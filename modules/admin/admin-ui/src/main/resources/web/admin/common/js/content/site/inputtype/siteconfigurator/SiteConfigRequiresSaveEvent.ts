module api.content.site.inputtype.siteconfigurator {

    import Content = api.content.Content;

    export class SiteConfigRequiresSaveEvent extends api.event.Event {

        private content: Content;

        constructor(content: Content) {
            super();
            this.content = content;
        }

        getContent(): Content {
            return this.content;
        }

        static on(handler: (event: SiteConfigRequiresSaveEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: SiteConfigRequiresSaveEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }
    }
}