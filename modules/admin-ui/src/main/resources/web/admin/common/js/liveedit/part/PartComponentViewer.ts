module api.liveedit.part {

    export class PartComponentViewer extends api.ui.Viewer<api.content.page.region.PartComponent> {

        private namesAndIconView: api.app.NamesAndIconView;

        constructor() {
            super();
            this.namesAndIconView = new api.app.NamesAndIconViewBuilder().setSize(api.app.NamesAndIconViewSize.small).build();
            this.appendChild(this.namesAndIconView);
        }

        setObject(partComponent: api.content.page.region.PartComponent) {
            super.setObject(partComponent);
            this.namesAndIconView.setMainName(partComponent.getName() ? partComponent.getName().toString() : "").
                setSubName(partComponent.getPath().toString()).
                setIconClass('live-edit-font-icon-part');
        }

        getPreferredHeight(): number {
            return 50;
        }
    }

}
