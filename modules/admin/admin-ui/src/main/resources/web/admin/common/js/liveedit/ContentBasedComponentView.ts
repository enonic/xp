module api.liveedit {

    import Component = api.content.page.region.Component;
    import ContentSummary = api.content.ContentSummary;
    import ContentSummaryBuilder = api.content.ContentSummaryBuilder;
    import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
    import ContentTypeName = api.schema.content.ContentTypeName;
    import i18n = api.util.i18n;

    export class ContentBasedComponentViewBuilder<COMPONENT extends Component> extends ComponentViewBuilder<COMPONENT> {

        contentTypeName: ContentTypeName;

        setContentTypeName(contentTypeName: ContentTypeName): ContentBasedComponentViewBuilder<COMPONENT> {
            this.contentTypeName = contentTypeName;
            return this;
        }
    }

    export class ContentBasedComponentView<COMPONENT extends Component> extends ComponentView<COMPONENT> {

        private contentTypeName: ContentTypeName;

        constructor(builder: ContentBasedComponentViewBuilder<COMPONENT>) {
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
            return new api.ui.Action(i18n('action.edit')).onExecuted(() => {
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
