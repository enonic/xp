module api.util.htmlarea.dialog {

    import ContentId = api.content.ContentId;
    import ContentPath = api.content.ContentPath;
    import ContentSummary = api.content.ContentSummary;

    export enum HtmlAreaDialogType {
        ANCHOR, IMAGE, LINK, MACRO
    }

    export class CreateHtmlAreaDialogEvent extends api.event.Event {

        private config: any;

        private type: HtmlAreaDialogType;

        private content: ContentSummary;

        constructor(builder: HtmlAreaDialogShownEventBuilder) {
            super();

            this.config = builder.config;
            this.type = builder.type;
            this.content = builder.content;
        }

        getConfig(): any {
            return this.config;
        }

        getType(): HtmlAreaDialogType {
            return this.type;
        }

        getContent(): ContentSummary {
            return this.content;
        }

        static create(): HtmlAreaDialogShownEventBuilder {
            return new HtmlAreaDialogShownEventBuilder();
        }

        static on(handler: (event: CreateHtmlAreaDialogEvent) => void, contextWindow: Window = window) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: CreateHtmlAreaDialogEvent) => void, contextWindow: Window = window) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }
    }

    export class HtmlAreaDialogShownEventBuilder {

        config: any;

        type: HtmlAreaDialogType;

        content: ContentSummary;

        setContent(content: ContentSummary): HtmlAreaDialogShownEventBuilder {
            this.content = content;
            return this;
        }

        setType(type: HtmlAreaDialogType): HtmlAreaDialogShownEventBuilder {
            this.type = type;
            return this;
        }

        setConfig(config: any): HtmlAreaDialogShownEventBuilder {
            this.config = config;
            return this;
        }

        build(): CreateHtmlAreaDialogEvent {

            return new CreateHtmlAreaDialogEvent(this);
        }
    }
}