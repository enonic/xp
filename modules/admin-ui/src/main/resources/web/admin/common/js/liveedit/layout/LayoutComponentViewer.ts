module api.liveedit.layout {

    export class LayoutComponentViewer extends api.ui.Viewer<api.content.page.region.LayoutComponent> {

        private namesAndIconView: api.app.NamesAndIconView;

        constructor() {
            super();
            this.namesAndIconView = new api.app.NamesAndIconViewBuilder().setSize(api.app.NamesAndIconViewSize.small).build();
            this.appendChild(this.namesAndIconView);
        }

        setObject(layoutComponent: api.content.page.region.LayoutComponent) {
            super.setObject(layoutComponent);
            this.namesAndIconView.setMainName(layoutComponent.getName().toString()).
                setSubName(layoutComponent.getPath().toString()).
                setIconClass('live-edit-font-icon-layout');
        }

        getPreferredHeight(): number {
            return 50;
        }
    }

}
