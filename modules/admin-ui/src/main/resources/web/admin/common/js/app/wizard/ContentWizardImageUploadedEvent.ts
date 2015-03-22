module api.app.wizard {

    export class ContentWizardImageUploadedEvent extends api.event.Event {
        private content: api.content.Content;
        private imageUploader: api.content.ImageUploader;

        constructor(content: api.content.Content, imageUploader: api.content.ImageUploader) {
            super();
            this.content = content;
            this.imageUploader = imageUploader;
        }

        getContent(): api.content.Content {
            return this.content;
        }

        getImageUploader(): api.content.ImageUploader {
            return this.imageUploader;
        }

        static on(handler: (event: ContentWizardImageUploadedEvent) => void, contextWindow: Window = window) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: ContentWizardImageUploadedEvent) => void, contextWindow: Window = window) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }
    }
}