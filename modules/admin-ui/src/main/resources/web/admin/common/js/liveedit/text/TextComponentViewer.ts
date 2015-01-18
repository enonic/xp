module api.liveedit.text {

    export class TextComponentViewer extends api.ui.Viewer<api.content.page.region.TextComponent> {

        private namesAndIconView: api.app.NamesAndIconView;

        constructor() {
            super();
            this.namesAndIconView = new api.app.NamesAndIconViewBuilder().setSize(api.app.NamesAndIconViewSize.small).build();
            this.appendChild(this.namesAndIconView);
        }

        setObject(textComponent: api.content.page.region.TextComponent) {
            super.setObject(textComponent);
            this.namesAndIconView.setMainName(textComponent.getText()).
                setSubName(textComponent.getPath().toString()).
                setIconClass('live-edit-font-icon-text');
            return this;
        }

        getPreferredHeight(): number {
            return 50;
        }
    }

}
