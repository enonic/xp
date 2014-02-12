module app.contextwindow {

    import SiteTemplate = api.content.site.template.SiteTemplate;

    export interface ImageInspectionPanelConfig {
        siteTemplate:SiteTemplate;
        liveFormPanel:app.wizard.LiveFormPanel;
    }

    export class ImageInspectionPanel extends api.ui.Panel {
        private siteTemplate: SiteTemplate;
        private nameAndIcon: api.app.NamesAndIconView;
        private liveFormPanel: app.wizard.LiveFormPanel;

        constructor(config: ImageInspectionPanelConfig) {
            super("detail-panel");

            this.siteTemplate = config.siteTemplate;
            this.liveFormPanel = config.liveFormPanel;

            this.initElements();
            this.setEmpty();

            SelectComponentEvent.on((event) => {
                this.setName(event.getComponent().name);
                this.setType(event.getComponent().componentType.typeName);
                this.setIcon(event.getComponent().componentType.iconCls);
            });

            ComponentDeselectEvent.on((event) => {
                this.setEmpty();
            });

        }

        private initElements() {
            this.nameAndIcon =
            new api.app.NamesAndIconView(new api.app.NamesAndIconViewBuilder().setSize(api.app.NamesAndIconViewSize.medium));

            var imageDescriptorsRequest = new api.content.page.image.GetImageDescriptorsByModulesRequest(this.siteTemplate.getModules());
            var imageDescriptorLoader = new api.content.page.image.ImageDescriptorLoader(imageDescriptorsRequest);
            var descriptorComboBox = new api.content.page.image.ImageDescriptorComboBox(imageDescriptorLoader);

            var firstLoad = (modules) => {
                descriptorComboBox.setValue(this.liveFormPanel.getDefaultImageDescriptor().getKey().toString());
                descriptorComboBox.removeLoadedListener(firstLoad);
            };
            descriptorComboBox.addLoadedListener(firstLoad);


            var templateHeader = new api.dom.H6El();
            templateHeader.setText("Template:");
            templateHeader.addClass("template-header");

            this.appendChild(this.nameAndIcon);

            this.appendChild(templateHeader);
            this.appendChild(descriptorComboBox);
        }

        private setEmpty() {
            this.nameAndIcon.setMainName("Empty");
            this.nameAndIcon.setMainName("No component selected");
            this.nameAndIcon.setIconUrl("");
        }

        setIcon(iconCls: string) {
            this.nameAndIcon.setIconClass(iconCls);
        }

        setName(name: string) {
            this.nameAndIcon.setMainName(name);
        }

        setType(type: string) {
            this.nameAndIcon.setSubName(type);
        }
    }
}