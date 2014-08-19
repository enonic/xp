module app.browse.event {

    import Event = api.event.Event;

    export class DeleteTemplatePromptEvent extends Event {

        private template: app.browse.TemplateSummary;

        constructor(template: app.browse.TemplateSummary) {
            this.template = template;
            super('deleteSiteTemplatePrompt');
        }

        getTemplate(): app.browse.TemplateSummary {
            return this.template;
        }

        static on(handler: (event: DeleteTemplatePromptEvent) => void, contextWindow: Window = window) {
            Event.bind("deleteSiteTemplatePrompt", handler, contextWindow);
        }

        static un(handler: (event: DeleteTemplatePromptEvent) => void, contextWindow: Window = window) {
            Event.unbind("deleteSiteTemplatePrompt", handler, contextWindow);
        }
    }

}