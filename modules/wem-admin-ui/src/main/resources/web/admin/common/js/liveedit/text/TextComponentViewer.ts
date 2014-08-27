module api.liveedit.text {

    export class TextComponentViewer extends api.ui.Viewer<api.content.page.text.TextComponent> {

        private static MAX_TOOLTIP_LENGTH : number = 30;

        private namesAndIconView: api.app.NamesAndIconView;
        constructor() {
            super();
            this.namesAndIconView = new api.app.NamesAndIconViewBuilder().setSize(api.app.NamesAndIconViewSize.small).build();
            this.appendChild(this.namesAndIconView);
        }

        setObject(textComponent: api.content.page.text.TextComponent) {
            super.setObject(textComponent);
            this.namesAndIconView.setMainName(this.getComponentTextValue(textComponent)).
                setSubName(textComponent.getPath().toString()).
                setIconClass('live-edit-font-icon-text');
            return this;
        }

        getPreferredHeight(): number {
            return 50;
        }

        private getComponentTextValue(textComponent: api.content.page.text.TextComponent) : string {
            var textComponentValue: string = textComponent.getText();
            if(textComponentValue && textComponentValue.length > TextComponentViewer.MAX_TOOLTIP_LENGTH) {
                textComponentValue = textComponentValue.substring(0,TextComponentViewer.MAX_TOOLTIP_LENGTH)+"...";
            }
            return textComponentValue;
        }
    }

}
