module api.liveedit {

    import Content = api.content.Content;
    import SiteTemplate = api.content.site.template.SiteTemplate;

    export class InitializeLiveEditEvent extends api.event.Event2 {

        private content: Content;

        private siteTemplate: SiteTemplate;

        constructor(content: Content, siteTemplate: SiteTemplate) {
            super();
            this.content = content;
            this.siteTemplate = siteTemplate;
        }

        getContent(): Content {
            return this.content;
        }

        getSiteTemplate(): SiteTemplate {
            return this.siteTemplate;
        }

        static on(handler: (event: InitializeLiveEditEvent) => void, contextWindow: Window = window) {
            api.event.Event2.bind(api.util.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: InitializeLiveEditEvent) => void, contextWindow: Window = window) {
            api.event.Event2.unbind(api.util.getFullName(this), handler, contextWindow);
        }
    }
}