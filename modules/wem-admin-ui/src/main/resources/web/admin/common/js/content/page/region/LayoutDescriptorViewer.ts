module api.content.page.region {

    export class LayoutDescriptorViewer extends api.ui.Viewer<LayoutDescriptor> {

        private namesAndIconView: api.app.NamesAndIconView;

        constructor() {
            super();
            this.namesAndIconView = new api.app.NamesAndIconViewBuilder().
                setSize(api.app.NamesAndIconViewSize.small).build();
            this.namesAndIconView.setIconClass('icon-insert-template icon-large');
            this.appendChild(this.namesAndIconView);
        }

        setObject(layoutDescriptor: LayoutDescriptor) {
            super.setObject(layoutDescriptor);
            this.namesAndIconView.setMainName(layoutDescriptor.getDisplayName()).
                setSubName(layoutDescriptor.getKey().toString());
        }

        getPreferredHeight(): number {
            return 50;
        }
    }
}