module api.content {

    export class ContentPublishedEvent extends api.event.Event {

        private content: api.content.Content;
        private wizard: api.app.wizard.WizardPanel<Content>;

        constructor(content: api.content.Content) {
            super();
            this.content = content;
        }

        public getContent(): api.content.Content {
            return this.content;
        }

        public getWizard(): api.app.wizard.WizardPanel<Content> {
            return this.wizard;
        }

        static on(handler: (event: ContentUpdatedEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: ContentUpdatedEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }
    }
}