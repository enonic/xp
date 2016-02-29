module app.wizard.page.contextwindow {

    import LiveFormPanel = app.wizard.page.LiveFormPanel;
    import LiveEditPage = app.wizard.page.LiveEditPageProxy;
    import PageTemplateKey = api.content.page.PageTemplateKey;
    import PageTemplate = api.content.page.PageTemplate;
    import PageDescriptor = api.content.page.PageDescriptor;
    import Region = api.content.page.region.Region;
    import ImageComponent = api.content.page.region.ImageComponent;
    import ImageComponentBuilder = api.content.page.region.ImageComponentBuilder;
    import ResponsiveManager = api.ui.responsive.ResponsiveManager;
    import ResponsiveItem = api.ui.responsive.ResponsiveItem;
    import BaseInspectionPanel = app.wizard.page.contextwindow.inspect.BaseInspectionPanel;
    import InspectionsPanel = app.wizard.page.contextwindow.inspect.InspectionsPanel;
    import InsertablesPanel = app.wizard.page.contextwindow.insert.InsertablesPanel;
    import ContentWizardPanel = app.wizard.ContentWizardPanel;

    export interface ContextWindowConfig {

        liveEditPage: LiveEditPageProxy;

        liveFormPanel:LiveFormPanel;

        inspectionPanel:InspectionsPanel;

        emulatorPanel:EmulatorPanel;

        insertablesPanel:InsertablesPanel;
    }

    export class ContextWindow extends api.ui.panel.DockedPanel {

        private insertablesPanel: InsertablesPanel;

        private inspectionsPanel: InspectionsPanel;

        private emulatorPanel: EmulatorPanel;

        private liveFormPanel: LiveFormPanel;

        private contextWindowState: ContextWindowState = ContextWindowState.HIDDEN;

        private splitter: api.dom.DivEl;

        private ghostDragger: api.dom.DivEl;

        private mask: api.ui.mask.DragMask;

        private actualWidth: number;

        private minWidth: number = 280;

        private parentMinWidth: number = 15;

        private displayModeChangedListeners: {() : void}[] = [];

        private animationTimer;

        private fixed: boolean = false;

        constructor(config: ContextWindowConfig) {
            super();
            this.liveFormPanel = config.liveFormPanel;
            this.inspectionsPanel = config.inspectionPanel;
            this.emulatorPanel = config.emulatorPanel;
            this.insertablesPanel = config.insertablesPanel;

            this.addClass("context-window");

            this.ghostDragger = new api.dom.DivEl("ghost-dragger");
            this.splitter = new api.dom.DivEl("splitter");

            ResponsiveManager.onAvailableSizeChanged(this.liveFormPanel, (item: ResponsiveItem) => {
                this.updateFrameSize();
            });

            this.appendChild(this.splitter);
            this.addItem("Insert", false, this.insertablesPanel);
            this.addItem("Inspect", false, this.inspectionsPanel);
            this.addItem("Emulator", false, this.emulatorPanel);

            this.onRendered(() => this.onRenderedHandler());

            this.onRemoved((event) => {
                ResponsiveManager.unAvailableSizeChanged(this);
                ResponsiveManager.unAvailableSizeChanged(this.liveFormPanel);
            });

            this.insertablesPanel.getComponentsView().onBeforeInsertAction(() => {
                this.fixed = true;
            });
        }

        getComponentsView(): app.wizard.PageComponentsView {
            return this.insertablesPanel.getComponentsView();
        }

        private onRenderedHandler() {
            var initialPos = 0;
            var splitterPosition = 0;
            var parent = this.getParentElement();
            this.actualWidth = this.getEl().getWidth();
            this.mask = new api.ui.mask.DragMask(parent);

            var dragListener = (e: MouseEvent) => {
                if (this.splitterWithinBoundaries(initialPos - e.clientX)) {
                    splitterPosition = e.clientX;
                    this.ghostDragger.getEl().setLeftPx(e.clientX - this.getEl().getOffsetLeft());
                }
            };

            this.splitter.onMouseDown((e: MouseEvent) => {
                e.preventDefault();
                initialPos = e.clientX;
                splitterPosition = e.clientX;
                this.startDrag(dragListener);
            });

            this.mask.onMouseUp((e: MouseEvent) => {
                this.actualWidth = this.getEl().getWidth() + initialPos - splitterPosition;
                this.stopDrag(dragListener);
                ResponsiveManager.fireResizeEvent();
            });

            // hide itself after all calculations have been made
            this.addClass('hidden');
        }

        private splitterWithinBoundaries(offset: number) {
            var newWidth = this.actualWidth + offset;
            return (newWidth >= this.minWidth) && (newWidth <= this.getParentElement().getEl().getWidth() - this.parentMinWidth);
        }

        private startDrag(dragListener: {(e: MouseEvent):void}) {
            this.mask.show();
            this.mask.onMouseMove(dragListener);
            this.ghostDragger.insertBeforeEl(this.splitter);
            this.ghostDragger.getEl().setLeftPx(this.splitter.getEl().getOffsetLeftRelativeToParent()).setTop(null);
        }

        private stopDrag(dragListener: {(e: MouseEvent):void}) {
            this.getEl().setWidthPx(this.actualWidth);
            this.mask.unMouseMove(dragListener);
            this.mask.hide();
            this.removeChild(this.ghostDragger);
        }

        isShown(): boolean {
            return this.contextWindowState == ContextWindowState.SHOWN;
        }

        isSlidingIn(): boolean {
            return this.contextWindowState == ContextWindowState.SLIDING_IN;
        }

        isSlidingOut(): boolean {
            return this.contextWindowState == ContextWindowState.SLIDING_OUT;
        }


        isFixed(): boolean {
            return this.fixed;
        }

        setFixed(value: boolean) {
            this.fixed = value;
        }

        isShownOrAboutToBeShown(): boolean {
            return this.isShown() || this.isSlidingIn();
        }

        slideOut() {
            this.getEl().setRightPx(-this.getEl().getWidthWithBorder());
            // there is a 100ms animation so wait until it's finished
            if (this.animationTimer) {
                clearTimeout(this.animationTimer);
            }
            this.contextWindowState = ContextWindowState.SLIDING_OUT;
            this.animationTimer = setTimeout(() => {
                this.getEl().addClass('hidden');
                this.contextWindowState = ContextWindowState.HIDDEN;
                this.updateFrameSize();
                this.animationTimer = null;
            }, 100);
        }

        slideIn() {
            this.getEl().removeClass('hidden').setRightPx(0);
            // there is a 100ms animation so wait until it's finished
            if (this.animationTimer) {
                clearTimeout(this.animationTimer);
            }
            this.contextWindowState = ContextWindowState.SLIDING_IN;
            this.animationTimer = setTimeout(() => {
                this.contextWindowState = ContextWindowState.SHOWN;
                this.updateFrameSize();
                this.animationTimer = null;
            }, 100);
        }

        public showInspectionPanel(panel: BaseInspectionPanel) {
            this.inspectionsPanel.showInspectionPanel(panel);
            this.selectPanel(this.inspectionsPanel);
        }

        public clearSelection() {
            this.inspectionsPanel.clearInspection();
            this.selectPanel(this.insertablesPanel);
        }

        public isInspecting(): boolean {
            return this.inspectionsPanel.isInspecting();
        }

        private updateFrameSize() {
            var isFloating = this.isFloating(),
                displayModeChanged = this.hasClass('floating') && !isFloating,
                contextWindowWidth = this.actualWidth || this.getEl().getWidth();

            this.liveFormPanel.updateFrameContainerSize(!isFloating && this.isShown(), contextWindowWidth);

            this.toggleClass("floating", isFloating);

            if (displayModeChanged) {
                this.notifyDisplayModeChanged();
            }
        }

        isLiveFormShown(): boolean {
            return this.liveFormPanel.isVisible();
        }

        isFloating(): boolean {
            var contextWindowWidth = this.actualWidth || this.getEl().getWidth();
            var liveFormPanelWidth = this.liveFormPanel.getEl().getWidth();
            return (liveFormPanelWidth < 1200) || ((liveFormPanelWidth - contextWindowWidth) < 920);
        }

        notifyDisplayModeChanged() {
            this.displayModeChangedListeners.forEach((listener: ()=> void) => listener());
        }

        onDisplayModeChanged(listener: () => void) {
            this.displayModeChangedListeners.push(listener);
        }

        unDisplayModeChanged(listener: () => void) {
            this.displayModeChangedListeners = this.displayModeChangedListeners.filter((currentListener: () => void) => {
                return listener != currentListener;
            });
        }

    }

    enum ContextWindowState {
        SHOWN, HIDDEN, SLIDING_IN, SLIDING_OUT
    }
}
