module app.contextwindow {

    import SiteTemplate = api.content.site.template.SiteTemplate;

    export interface ComponentInspectionPanelConfig {
        siteTemplate:SiteTemplate;
        liveFormPanel:app.wizard.LiveFormPanel;
    }

    export class BaseComponentInspectionPanel extends api.ui.Panel {

        private siteTemplate: SiteTemplate;
        private nameAndIcon: api.app.NamesAndIconView;
        private liveFormPanel: app.wizard.LiveFormPanel;
        private iconClass: string;

        constructor(config: ComponentInspectionPanelConfig, iconClass: string) {
            super("inspection-panel");

            this.siteTemplate = config.siteTemplate;
            this.liveFormPanel = config.liveFormPanel;
            this.iconClass = iconClass;

            this.nameAndIcon =
            new api.app.NamesAndIconView(new api.app.NamesAndIconViewBuilder().
                setSize(api.app.NamesAndIconViewSize.medium)).
                setIconClass(this.iconClass);

            this.appendChild(this.nameAndIcon);
        }

        getLiveFormPanel(): app.wizard.LiveFormPanel {
            return this.liveFormPanel;
        }

        getSiteTemplate(): SiteTemplate {
            return this.siteTemplate;
        }

        setName(name: string, subName: string) {
            this.nameAndIcon.setMainName(name);
            this.nameAndIcon.
                setMainName(name).
                setSubName(subName);
        }

    }
}