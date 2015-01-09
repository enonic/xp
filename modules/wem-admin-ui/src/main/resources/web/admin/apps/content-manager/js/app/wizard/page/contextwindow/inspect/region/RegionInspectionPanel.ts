module app.wizard.page.contextwindow.inspect.region {

    import Region = api.content.page.region.Region;

    export class RegionInspectionPanel extends BaseInspectionPanel {

        private region: Region;

        private namesAndIcon: api.app.NamesAndIconView;

        constructor() {
            super();

            this.namesAndIcon = new api.app.NamesAndIconView(new api.app.NamesAndIconViewBuilder().
                setSize(api.app.NamesAndIconViewSize.medium)).
                setIconClass("live-edit-font-icon-region icon-xlarge");

            this.appendChild(this.namesAndIcon);
        }

        setRegion(region: Region) {

            this.region = region;

            if (region) {
                this.namesAndIcon.setMainName(region.getName() );
                this.namesAndIcon.setSubName(region.getPath().toString());
            }
            else {
                this.namesAndIcon.setMainName("[No  Region given]" );
                this.namesAndIcon.setSubName("");
            }
        }

    }
}
