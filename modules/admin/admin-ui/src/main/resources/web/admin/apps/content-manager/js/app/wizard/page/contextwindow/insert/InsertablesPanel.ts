module app.wizard.page.contextwindow.insert {

    import DragHelper = api.ui.DragHelper;
    import PageView = api.liveedit.PageView;
    import LiveEditPageViewReadyEvent = api.liveedit.LiveEditPageViewReadyEvent;
    import Content = api.content.Content;

    export interface ComponentTypesPanelConfig {

        liveEditPage: app.wizard.page.LiveEditPageProxy;

        contentWizardPanel: ContentWizardPanel;
    }

    export class InsertablesPanel extends api.ui.panel.Panel {

        private liveEditPageProxy: app.wizard.page.LiveEditPageProxy;

        private insertablesGrid: InsertablesGrid;

        private insertablesDataView: api.ui.grid.DataView<Insertable>;

        private hideContextWindowRequestListeners: {(): void;}[] = [];

        private pageView: PageView;

        private componentsView: app.wizard.PageComponentsView;

        private overIFrame: boolean = false;

        private iFrameDraggable: JQuery;

        private contextWindowDraggable: JQuery;

        public static debug = false;

        constructor(config: ComponentTypesPanelConfig) {
            super("insertables-panel");
            this.liveEditPageProxy = config.liveEditPage;

            var topDescription = new api.dom.PEl();
            topDescription.getEl().setInnerHtml('Drag and drop components into the page');

            this.insertablesDataView = new api.ui.grid.DataView<Insertable>();
            this.insertablesGrid = new InsertablesGrid(this.insertablesDataView, {draggableRows: true, rowClass: "comp"});

            this.insertablesDataView.setItems(Insertables.ALL, "name");

            this.componentsView = new app.wizard.PageComponentsView(config.liveEditPage);

            this.componentsView.onShown(() => {
                if (!this.componentsView.getParentElement()) {
                    //append it on click only to be sure that content wizard panel is ready
                    var offset = config.contentWizardPanel.getLiveFormPanel().getEl().getOffsetToParent();
                    this.componentsView.getEl().setOffset(offset);
                    config.contentWizardPanel.appendChild(this.componentsView);
                }
            });

            this.appendChildren(topDescription, this.insertablesGrid);

            this.liveEditPageProxy.onLiveEditPageViewReady((event: LiveEditPageViewReadyEvent) => {
                this.pageView = event.getPageView();
            });

            this.liveEditPageProxy.onComponentViewDragStopped(() => {
                // Drop was performed on live edit page
                if (this.contextWindowDraggable) {
                    if (InsertablesPanel.debug) {
                        console.log('Simulating mouse up for', this.contextWindowDraggable);
                    }
                    this.contextWindowDraggable.simulate('mouseup');
                }
            });

            this.onRendered(this.initializeDraggables.bind(this));
            this.onRemoved(this.destroyDraggables.bind(this));
        }

        getComponentsView(): app.wizard.PageComponentsView {
            return this.componentsView;
        }

        setPageView(pageView: api.liveedit.PageView) {
            this.componentsView.setPageView(pageView);
        }

        setContent(content: Content) {
            this.componentsView.setContent(content);
        }

        private initializeDraggables() {
            var components = wemjq('[data-context-window-draggable="true"]:not(.ui-draggable)');

            if (InsertablesPanel.debug) {
                console.log('InsertablesPanel.initializeDraggables', components);
            }

            components.draggable({
                cursorAt: DragHelper.CURSOR_AT,
                appendTo: 'body',
                cursor: 'move',
                revert: 'true',
                distance: 10,
                scope: 'component',
                helper: (event, ui) => DragHelper.get().getHTMLElement(),
                start: (event, ui) => this.handleDragStart(event, ui),
                drag: (event, ui) => this.handleDrag(event, ui),
                stop: (event, ui) => this.handleDragStop(event, ui)
            });
        }

        private destroyDraggables() {
            var components = wemjq('[data-context-window-draggable="true"]:not(.ui-draggable)');

            components.draggable('destroy');
        }

        private handleDragStart(event: Event, ui: JQueryUI.DraggableEventUIParams) {
            if (InsertablesPanel.debug) {
                console.log('InsertablesPanel.handleDragStart', event, ui);
            }

            ui.helper.show();

            this.liveEditPageProxy.getDragMask().show();

            // force the lock mask to be shown
            this.contextWindowDraggable = wemjq(event.target);
        }

        private handleDrag(event: Event, ui: JQueryUI.DraggableEventUIParams) {
            var over = this.isOverIFrame(event);
            if (this.overIFrame != over) {
                if (over) {
                    this.onEnterIFrame(event, ui);
                } else {
                    this.onLeftIFrame(event, ui);
                }
                this.overIFrame = over;
            }
        }

        private handleDragStop(event: Event, ui: JQueryUI.DraggableEventUIParams) {
            if (InsertablesPanel.debug) {
                console.log('InsertablesPanel.handleDragStop', event, ui);
            }
            this.liveEditPageProxy.getDragMask().hide();
            // remove forced lock mask
            this.contextWindowDraggable = null;

            if (this.iFrameDraggable) {
                this.iFrameDraggable.simulate('mouseup');
                this.liveEditPageProxy.destroyDraggable(this.iFrameDraggable);
                this.iFrameDraggable.remove();
                this.iFrameDraggable = null;
            }
        }

        private isOverIFrame(event: Event): boolean {
            return event.target == this.liveEditPageProxy.getDragMask().getHTMLElement();
        }

        private onLeftIFrame(event: Event, ui: JQueryUI.DraggableEventUIParams) {
            if (InsertablesPanel.debug) {
                console.log('InsertablesPanel.onLeftIFrame');
            }
            this.liveEditPageProxy.getDragMask().show();

            if (this.iFrameDraggable) {
                var livejq = this.liveEditPageProxy.getJQuery();
                // hide the helper of the iframe draggable,
                // it's a function so call it to get element and wrap in jquery to hide
                livejq(this.iFrameDraggable.draggable("option", "helper")()).hide();
            }

            // and show the one in the parent
            ui.helper.show();
        }

        private onEnterIFrame(event: Event, ui: JQueryUI.DraggableEventUIParams) {
            if (InsertablesPanel.debug) {
                console.log('InsertablesPanel.onEnterIFrame');
            }
            this.liveEditPageProxy.getDragMask().hide();
            var livejq = this.liveEditPageProxy.getJQuery();

            var iFrame = <HTMLIFrameElement>this.liveEditPageProxy.getIFrame().getHTMLElement();
            var hasBody = iFrame && iFrame.contentDocument && iFrame.contentDocument.body;
            if (!hasBody) {
                if (InsertablesPanel.debug) {
                    console.warn('InsertablesPanel.onEnterIFrame, skip due to missing body in document');
                }
                return;
            }

            if (!this.iFrameDraggable) {
                this.iFrameDraggable = livejq(event.target).clone();
                livejq('body').append(this.iFrameDraggable);
                this.liveEditPageProxy.createDraggable(this.iFrameDraggable);
                this.iFrameDraggable.simulate('mousedown').hide();
            }

            // show the helper of the iframe draggable
            // it's a function so call it to get element and wrap in jquery to show
            livejq(this.iFrameDraggable.draggable("option", "helper")()).show();

            // and hide the one in the parent
            ui.helper.hide();

            this.notifyHideContextWindowRequest();
        }

        onHideContextWindowRequest(listener: {(): void;}) {
            this.hideContextWindowRequestListeners.push(listener);
        }

        unHideContextWindowRequest(listener: {(): void;}) {
            this.hideContextWindowRequestListeners = this.hideContextWindowRequestListeners.filter(function (curr) {
                return curr != listener;
            });
        }

        private notifyHideContextWindowRequest() {
            this.hideContextWindowRequestListeners.forEach((listener) => {
                listener();
            });
        }
    }
}