module api.content.page {

    export class PageTemplateViewer extends api.ui.Viewer<PageTemplate> {

        private namesAndIconView: api.app.NamesAndIconView;

        constructor() {
            super();
            this.namesAndIconView = new api.app.NamesAndIconViewBuilder().setSize(api.app.NamesAndIconViewSize.small).build();
            this.namesAndIconView.setIconClass("icon-newspaper icon-large");
            this.appendChild(this.namesAndIconView);
        }

        setObject(pageTemplate: PageTemplate) {
            super.setObject(pageTemplate);
            this.namesAndIconView.setMainName(pageTemplate.getDisplayName()).
                setSubName(pageTemplate.getController().toString());
        }

        getPreferredHeight(): number {
            return 50;
        }
    }
}