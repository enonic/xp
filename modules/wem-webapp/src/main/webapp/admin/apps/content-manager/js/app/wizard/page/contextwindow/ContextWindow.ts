module app.wizard.page.contextwindow {

    import RootDataSet = api.data.RootDataSet;
    import LiveFormPanel = app.wizard.page.LiveFormPanel;
    import ComponentPath = api.content.page.ComponentPath;
    import Content = api.content.Content;
    import PageTemplateKey = api.content.page.PageTemplateKey;
    import PageTemplate = api.content.page.PageTemplate;
    import PageDescriptor = api.content.page.PageDescriptor;
    import PageComponent = api.content.page.PageComponent;
    import Region = api.content.page.region.Region;
    import ImageComponent = api.content.page.image.ImageComponent;
    import ImageComponentBuilder = api.content.page.image.ImageComponentBuilder;
    import BaseInspectionPanel = app.wizard.page.contextwindow.inspect.BaseInspectionPanel;
    import InspectionPanel = app.wizard.page.contextwindow.inspect.InspectionPanel;
    import InsertablesPanel = app.wizard.page.contextwindow.insert.InsertablesPanel;

    export interface ContextWindowConfig {

        liveEditPage: app.wizard.page.LiveEditPage;

        liveFormPanel:LiveFormPanel;

        inspectionPanel:InspectionPanel;

        emulatorPanel:EmulatorPanel;

        insertablesPanel:InsertablesPanel;
    }

    export class ContextWindow extends api.ui.DockedWindow {

        private insertablesPanel: InsertablesPanel;

        private inspectionPanel: InspectionPanel;

        private emulatorPanel: EmulatorPanel;

        private liveEditPage: app.wizard.page.LiveEditPage;

        private liveFormPanel: LiveFormPanel;

        private pinned: boolean;

        constructor(config: ContextWindowConfig) {
            super();

            this.pinned = true;
            this.liveEditPage = config.liveEditPage;
            this.liveFormPanel = config.liveFormPanel;
            this.inspectionPanel = config.inspectionPanel;
            this.emulatorPanel = config.emulatorPanel;
            this.insertablesPanel = config.insertablesPanel;

            this.addClass("context-window");

            if (this.pinned) {
                this.liveFormPanel.resizeFrameContainer($(window).width() - 280);
            }

            this.insertablesPanel.onHideContextWindowRequest(() => {
                this.hide();
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

        public showInspectionPanel(panel: BaseInspectionPanel) {
            this.inspectionPanel.showInspectionPanel(panel);
            this.selectPanel(this.inspectionPanel);
        }

        public clearSelection() {
            this.inspectionPanel.clearSelection();
            this.selectPanel(this.insertablesPanel);
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
