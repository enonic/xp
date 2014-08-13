module api.content.form.inputtype.image {

    export class ImageSelectorViewer extends api.ui.Viewer<ImageSelectorDisplayValue> {

        private namesAndIconView: api.app.NamesAndIconView;

        constructor() {
            super();
            this.namesAndIconView = new api.app.NamesAndIconViewBuilder().setSize(api.app.NamesAndIconViewSize.small).build();
            this.appendChild(this.namesAndIconView);
        }

        setObject(value: ImageSelectorDisplayValue) {
            super.setObject(value);
            this.namesAndIconView.setMainName(value.getDisplayName()).
                setSubName(value.getPath()).
                setIconUrl(value.getImageUrl() + '?crop=false');
        }

        getPreferredHeight(): number {
            return 50;
        }
    }
}