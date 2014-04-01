module api.content.page.layout {

    export class LayoutDescriptorViewer extends api.ui.Viewer<LayoutDescriptor> {

        private namesAndIconView: api.app.NamesAndIconView;

        constructor() {
            super();
            this.namesAndIconView = new api.app.NamesAndIconViewBuilder().
                setSize(api.app.NamesAndIconViewSize.small).build();
            this.namesAndIconView.setIconClass('live-edit-font-icon-layout live-edit-combo-font-icon');
            this.appendChild(this.namesAndIconView);
        }

        setObject(layoutDescriptor: LayoutDescriptor) {
            super.setObject(layoutDescriptor);
            this.namesAndIconView.setMainName(layoutDescriptor.getDisplayName()).
                setSubName(layoutDescriptor.getName().toString());
        }

        getPreferredHeight(): number {
            return 50;
        }
    }
}