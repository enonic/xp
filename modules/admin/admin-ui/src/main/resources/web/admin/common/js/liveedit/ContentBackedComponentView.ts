module api.liveedit {

    import Component = api.content.page.region.Component;
    import ContentSummary = api.content.ContentSummary;
    import ContentSummaryBuilder = api.content.ContentSummaryBuilder;
    import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
    import ContentTypeName = api.schema.content.ContentTypeName;

    export class ContentBackedComponentViewBuilder<COMPONENT extends Component> extends ComponentViewBuilder<COMPONENT> {

        contentTypeName: ContentTypeName;

        constructor() {
            super();
        }

        setContentTypeName(contentTypeName: ContentTypeName): ContentBackedComponentViewBuilder<COMPONENT> {
            this.contentTypeName = contentTypeName;
            return this;
        }
    }

    export class ContentBackedComponentView<COMPONENT extends Component> extends ComponentView<COMPONENT> {

        private contentTypeName: ContentTypeName;

        constructor(builder: ContentBackedComponentViewBuilder<COMPONENT>) {
            super(builder);

            this.contentTypeName = builder.contentTypeName;

            this.addEditActionToMenu();
        }

        private addEditActionToMenu() {
            if (!this.isEmpty()) {
                this.addContextMenuActions([this.createEditAction()]);
            }
        }

        private createEditAction(): api.ui.Action {
            return new api.ui.Action('Edit').onExecuted(() => {
                new api.content.event.EditContentEvent([this.generateContentSummaryAndCompareStatus()]).fire();
            });
        }

        private generateContentSummaryAndCompareStatus() {
            const contentId: ContentId = this.getContentId();
            const contentSummary: ContentSummary = new ContentSummaryBuilder().setId(contentId.toString()).setContentId(contentId).setType(
                this.contentTypeName).build();

            return ContentSummaryAndCompareStatus.fromContentSummary(contentSummary);
        }

        protected getContentId(): ContentId {
            throw new Error('Must be implemented by inheritors');
        }
    }
}
