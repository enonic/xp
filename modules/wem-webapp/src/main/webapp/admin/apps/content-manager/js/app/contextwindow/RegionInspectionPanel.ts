module app.contextwindow {

    export class RegionInspectionPanel extends app.contextwindow.BaseComponentInspectionPanel {

        private region: api.content.page.region.Region;

        constructor(config: ComponentInspectionPanelConfig) {
            super(config, "live-edit-font-icon-region");

        }

        setRegion(region: api.content.page.region.Region) {
            this.region = region;
            if (region) {
                this.setName(region.getName(), 'region'); //region.getPath().toString());
            } else {
                this.setName('[No Name]', 'region');
            }
        }

    }
}
