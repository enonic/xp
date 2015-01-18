module api.content.page.region {

    export class PartDescriptorViewer extends api.ui.Viewer<PartDescriptor> {

        private namesAndIconView: api.app.NamesAndIconView;

        constructor() {
            super();
            this.namesAndIconView = new api.app.NamesAndIconViewBuilder().setSize(api.app.NamesAndIconViewSize.small).build()
                .setIconClass("icon-puzzle icon-large");
            this.appendChild(this.namesAndIconView);
        }

        setObject(partDescriptor: PartDescriptor) {
            super.setObject(partDescriptor);
            this.namesAndIconView.setMainName(partDescriptor.getDisplayName()).
                setSubName(partDescriptor.getKey().toString());
        }

        getPreferredHeight(): number {
            return 50;
        }
    }
}