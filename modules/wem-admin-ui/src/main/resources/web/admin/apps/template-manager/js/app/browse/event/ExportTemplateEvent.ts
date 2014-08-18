module app.browse.event {

    import Event = api.event.Event;

    export class ExportTemplateEvent extends Event {

        private siteTemplate: app.browse.TemplateSummary;

        constructor(siteTemplate: app.browse.TemplateSummary) {
            this.siteTemplate = siteTemplate;
            super('exportTemplate');
        }

        getTemplate(): app.browse.TemplateSummary {
            return this.siteTemplate;
        }

        static on(handler: (event: ExportTemplateEvent) => void, contextWindow: Window = window) {
            Event.bind("exportTemplate", handler, contextWindow);
        }

        static un(handler: (event: ExportTemplateEvent) => void, contextWindow: Window = window) {
            Event.unbind("exportTemplate", handler, contextWindow);
        }
    }
}