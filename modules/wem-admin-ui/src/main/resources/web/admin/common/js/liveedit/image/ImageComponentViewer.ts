module api.liveedit.image {

    export class ImageComponentViewer extends api.ui.Viewer<api.content.page.region.ImageComponent> {

        private namesAndIconView: api.app.NamesAndIconView;

        constructor() {
            super();
            this.namesAndIconView = new api.app.NamesAndIconViewBuilder().setSize(api.app.NamesAndIconViewSize.small).build();
            this.appendChild(this.namesAndIconView);
        }

        setObject(imageComponent: api.content.page.region.ImageComponent) {
            super.setObject(imageComponent);
            this.namesAndIconView.setMainName(imageComponent.getName().toString()).
                setSubName(imageComponent.getPath().toString()).
                setIconClass('live-edit-font-icon-image');
        }

        getPreferredHeight(): number {
            return 50;
        }

    }

}
