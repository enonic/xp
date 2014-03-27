module app.wizard.page.contextwindow {

    import RootDataSet = api.data.RootDataSet;
    import DefaultModels = app.wizard.page.DefaultModels;
    import LiveFormPanel = app.wizard.page.LiveFormPanel;
    import ComponentPath = api.content.page.ComponentPath;
    import Content = api.content.Content;
    import ContentTypeName = api.schema.content.ContentTypeName;
    import PageTemplateKey = api.content.page.PageTemplateKey;
    import PageTemplate = api.content.page.PageTemplate;
    import PageDescriptor = api.content.page.PageDescriptor;
    import PageComponent = api.content.page.PageComponent;
    import Region = api.content.page.region.Region;
    import ImageComponent = api.content.page.image.ImageComponent;
    import ImageComponentBuilder = api.content.page.image.ImageComponentBuilder;

    export interface ContextWindowConfig {
        liveEditIFrame?:api.dom.IFrameEl;
        liveEditId?:string;
        liveEditWindow: any;
        liveEditJQuery: JQueryStatic;
        siteTemplate:api.content.site.template.SiteTemplate;
        liveFormPanel:LiveFormPanel;
        contentType:ContentTypeName;
        defaultModels:DefaultModels;
    }

    export class ContextWindow extends api.ui.DockedWindow {

        private insertablesPanel: insert.InsertablesPanel;
        private inspectionPanel: inspect.InspectionPanel;
        private emulatorPanel: EmulatorPanel;
        private liveEditWindow: any;
        private liveEditJQuery: JQueryStatic;
        private dragMask: api.ui.DragMask;
        private liveEditIFrame: api.dom.IFrameEl;
        private liveFormPanel: LiveFormPanel;
        private pinned: boolean;

        constructor(config: ContextWindowConfig) {
            this.pinned = true;
            this.liveEditIFrame = config.liveEditIFrame;
            this.liveEditJQuery = config.liveEditJQuery;
            this.liveEditWindow = config.liveEditWindow;
            this.liveFormPanel = config.liveFormPanel;

            super();

            if (this.pinned) {
                this.liveFormPanel.resizeFrameContainer($(window).width() - 280);
            }

            this.addClass("context-window");

            this.dragMask = new api.ui.DragMask(this.liveEditIFrame);

            this.insertablesPanel = new insert.InsertablesPanel({
                contextWindow: this,
                liveEditIFrame: this.liveEditIFrame,
                liveEditWindow: this.liveEditWindow,
                liveEditJQuery: this.liveEditJQuery,
                draggingMask: this.dragMask //DragMask temporarily disabled
            });

            this.inspectionPanel = new inspect.InspectionPanel({
                liveEditWindow: this.liveEditWindow,
                siteTemplate: config.siteTemplate,
                liveFormPanel: this.liveFormPanel,
                contentType: config.contentType,
                defaultModels: config.defaultModels
            });
            this.emulatorPanel = new EmulatorPanel({
                liveEditIFrame: this.liveEditIFrame
            });

            app.wizard.ToggleContextWindowEvent.on(() => {
                if (!this.isEnabled()) {
                    this.enable();
                } else {
                    this.disable();
                }
                this.updateFrameSize();
            });

            $(window).resize(() => {
                this.updateFrameSize();
            })

            this.addItem("Insert", this.insertablesPanel);
            this.addItem("Settings", this.inspectionPanel);
            this.addItem("Emulator", this.emulatorPanel);

            var pinButton = new PinButton(this);
            this.appendChild(pinButton);

        }

        disable() {
            this.addClass("hidden");
            this.getEl().setRight("-290px");
        }

        enable() {
            this.removeClass("hidden");
            this.getEl().setRight("0px");
            api.dom.Body.get().appendChild(this.dragMask);
        }

        hide() {
            if (!this.pinned) {
                super.hide();
            }
        }

        show() {
            if (!this.pinned) {
                super.show();
            }
        }

        public inspectComponent(component: PageComponent) {
            this.inspectionPanel.inspectComponent(component);
            this.selectPanel(this.inspectionPanel);
        }

        public inspectPage(page: Content, pageTemplate: PageTemplate, pageDescriptor: PageDescriptor) {
            this.inspectionPanel.inspectPage(page, pageTemplate, pageDescriptor);
            this.selectPanel(this.inspectionPanel);
        }

        public inspectRegion(region: Region) {
            this.inspectionPanel.inspectRegion(region);
            this.selectPanel(this.inspectionPanel);
        }

        public inspectContent(content: Content) {
            this.inspectionPanel.inspectContent(content);
            this.selectPanel(this.inspectionPanel);
        }

        public clearSelection() {
            this.inspectionPanel.clearSelection();
            this.selectPanel(this.insertablesPanel);
        }

        setPage(page: Content, pageTemplate: PageTemplate, pageDescriptor: PageDescriptor) {
            this.inspectionPanel.setPage(page, pageTemplate, pageDescriptor);
        }

        getPageTemplate(): PageTemplateKey {
            return this.inspectionPanel.getPageTemplate();
        }

        getPageConfig(): RootDataSet {
            return this.inspectionPanel.getPageConfig();
        }

        setPinned(value: boolean) {
            this.pinned = value;
            this.updateFrameSize();
        }

        isPinned(): boolean {
            return this.pinned;
        }

        private isEnabled() {
            if (this.hasClass("hidden")) {
                return false;
            }
            return true;
        }

        private updateFrameSize() {
            if (this.pinned && this.isEnabled()) {
                this.liveFormPanel.resizeFrameContainer($(window).width() - 280);
            } else {
                this.liveFormPanel.resizeFrameContainer($(window).width());
            }
        }
    }

    export class PinButton extends api.ui.Button {
        constructor(contextWindow: ContextWindow) {
            super("")
            this.addClass("pin-button icon-pushpin");
            this.setActive(contextWindow.isPinned());

            this.onClicked((event: MouseEvent) => {
                contextWindow.setPinned(!contextWindow.isPinned());
                this.setActive(contextWindow.isPinned());
            });
        }
    }
}
