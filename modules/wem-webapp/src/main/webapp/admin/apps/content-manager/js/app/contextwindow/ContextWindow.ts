module app.contextwindow {

    import ComponentPath = api.content.page.ComponentPath;
    import ImageComponent = api.content.page.image.ImageComponent;
    import PageTemplateKey = api.content.page.PageTemplateKey;
    import ImageComponentBuilder = api.content.page.image.ImageComponentBuilder;

    export interface ContextWindowConfig {
        liveEditIFrame?:api.dom.IFrameEl;
        liveEditId?:string;
        liveEditWindow: any;
        liveEditJQuery: JQueryStatic;
        siteTemplate:api.content.site.template.SiteTemplate;
        liveFormPanel:app.wizard.LiveFormPanel;
    }

    export class ContextWindow extends api.ui.DockedWindow {
        private componentTypesPanel: ComponentTypesPanel;
        private inspectionPanel: InspectionPanel;
        private emulatorPanel: EmulatorPanel;
        private liveEditWindow: any;
        private liveEditJQuery: JQueryStatic;
        private draggingMask: api.ui.DraggingMask;
        private liveEditIFrame: api.dom.IFrameEl;
        private liveFormPanel: app.wizard.LiveFormPanel;

        constructor(config: ContextWindowConfig) {
            this.liveEditIFrame = config.liveEditIFrame;
            this.liveEditJQuery = config.liveEditJQuery;
            this.liveEditWindow = config.liveEditWindow;
            this.liveFormPanel = config.liveFormPanel;

            super();

            this.addClass("context-window");

            this.draggingMask = new api.ui.DraggingMask(this.liveEditIFrame);

            this.componentTypesPanel = new ComponentTypesPanel({
                contextWindow: this,
                liveEditIFrame: this.liveEditIFrame,
                liveEditWindow: this.liveEditWindow,
                liveEditJQuery: this.liveEditJQuery,
                draggingMask: this.draggingMask
            });

            this.inspectionPanel = new InspectionPanel({
                liveEditWindow: this.liveEditWindow,
                siteTemplate: config.siteTemplate,
                liveFormPanel: this.liveFormPanel
            });
            this.emulatorPanel = new EmulatorPanel({
                liveEditIFrame: this.liveEditIFrame
            });

            this.addItem("Insert", this.componentTypesPanel);
            this.addItem("Settings", this.inspectionPanel);
            this.addItem("Emulator", this.emulatorPanel);

            document.body.appendChild(this.draggingMask.getHTMLElement());
        }

        public inspectComponent(component: api.content.page.PageComponent) {
            this.inspectionPanel.inspectComponent(component);
            this.selectPanel(this.inspectionPanel);
        }

        public inspectPage(page: api.content.Content, pageTemplate: api.content.page.PageTemplate, pageDescriptor: api.content.page.PageDescriptor) {
            this.inspectionPanel.inspectPage(page, pageTemplate, pageDescriptor);
            this.selectPanel(this.inspectionPanel);
        }

        public inspectRegion(region: api.content.page.region.Region) {
            this.inspectionPanel.inspectRegion(region);
            this.selectPanel(this.inspectionPanel);
        }

        public inspectContent(content: api.content.Content) {
            this.inspectionPanel.inspectContent(content);
            this.selectPanel(this.inspectionPanel);
        }

        public clearSelection() {
            this.inspectionPanel.clearSelection();
            this.selectPanel(this.componentTypesPanel);
        }

    }
}