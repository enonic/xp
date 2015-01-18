module api.liveedit {

    export class RegionComponentViewer extends api.ui.Viewer<api.content.page.region.Region> {

        private namesAndIconView: api.app.NamesAndIconView;

        constructor() {
            super();
            this.namesAndIconView = new api.app.NamesAndIconViewBuilder().setSize(api.app.NamesAndIconViewSize.small).build();
            this.appendChild(this.namesAndIconView);
        }

        setObject(region: api.content.page.region.Region) {
            super.setObject(region);
            this.namesAndIconView.setMainName(region.getName().toString()).
                setSubName(region.getPath().toString()).
                setIconClass('live-edit-font-icon-region');
        }

        getPreferredHeight(): number {
            return 50;
        }
    }

}
