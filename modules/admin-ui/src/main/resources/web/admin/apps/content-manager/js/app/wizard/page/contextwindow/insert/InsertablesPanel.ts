module app.wizard.page.contextwindow.insert {

    import DragHelper = api.ui.DragHelper;

    export interface ComponentTypesPanelConfig {

        liveEditPage: app.wizard.page.LiveEditPageProxy;
    }

    export class InsertablesPanel extends api.ui.panel.Panel {

        private liveEditPageProxy: app.wizard.page.LiveEditPageProxy;

        private insertablesGrid: InsertablesGrid;

        private insertablesDataView: api.ui.grid.DataView<Insertable>;

        private hideContextWindowRequestListeners: {(): void;}[] = [];

        private overIFrame: boolean = false;

        private iFrameDraggable: JQuery;

        private contextWindowDraggable: JQuery;

        public static debug = true;

        constructor(config: ComponentTypesPanelConfig) {
            super("insertables-panel");
            this.liveEditPageProxy = config.liveEditPage;

            var topDescription = new api.dom.PEl();
            topDescription.getEl().setInnerHtml('Drag and drop components into the page');
            this.appendChild(topDescription);

            this.insertablesDataView = new api.ui.grid.DataView<Insertable>();
            this.insertablesGrid = new InsertablesGrid(this.insertablesDataView, {draggableRows: true, rowClass: "comp"});

            this.appendChild(this.insertablesGrid);
            this.insertablesDataView.setItems(Insertables.ALL, "name");

            this.liveEditPageProxy.onComponentViewDragStopped(() => {
                // Drop was performed on live edit page
                if (this.contextWindowDraggable) {
                    if (InsertablesPanel.debug) {
                        console.log('Simulating mouse up for', this.contextWindowDraggable);
                    }
                    this.contextWindowDraggable.simulate('mouseup');
                    this.contextWindowDraggable = null;
                }
            });

            this.onRendered(this.initializeDraggables.bind(this));
            this.onRemoved(this.destroyDraggables.bind(this));
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

        }

        private handleDragStart(event: JQueryEventObject, ui: JQueryUI.DraggableEventUIParams) {
            if (InsertablesPanel.debug) {
                console.log('handle drag start', event, ui);
            }
            this.liveEditPageProxy.showDragMask();
            this.contextWindowDraggable = wemjq(event.target);
        }

        private handleDrag(event: JQueryEventObject, ui: JQueryUI.DraggableEventUIParams) {
            var over = this.isOverIFrame(event);
            if (InsertablesPanel.debug) {
                console.log('Handle drag', event);
            }
            if (this.overIFrame != over) {
                if (over) {
                    this.onEnterIFrame(event, ui);
                } else {
                    this.onLeftIFrame(event, ui);
                }
                this.overIFrame = over;
            }
        }

        private handleDragStop(event: JQueryEventObject, ui: JQueryUI.DraggableEventUIParams) {
            if (InsertablesPanel.debug) {
                console.log('handle drag stop', event, ui);
            }
            this.liveEditPageProxy.hideDragMask();
            this.contextWindowDraggable = null;
        }

        private isOverIFrame(event: JQueryEventObject): boolean {
            return event.originalEvent.target == this.liveEditPageProxy.getDragMask().getHTMLElement();
        }

        private onLeftIFrame(event: Event, ui: JQueryUI.DraggableEventUIParams) {
            if (InsertablesPanel.debug) {
                console.log('Left LiveEdit');
            }
            this.liveEditPageProxy.showDragMask();

            if (this.iFrameDraggable) {
                this.iFrameDraggable.simulate('mouseup');
                this.liveEditPageProxy.destroyDraggable(this.iFrameDraggable);
                this.iFrameDraggable.remove();
            }

            ui.helper.show();
        }

        private onEnterIFrame(event: Event, ui: JQueryUI.DraggableEventUIParams) {
            if (InsertablesPanel.debug) {
                console.log('Left LiveEdit');
            }
            this.liveEditPageProxy.hideDragMask();

            ui.helper.hide();

            var livejq = this.liveEditPageProxy.getJQuery();
            this.iFrameDraggable = livejq(event.target).clone();
            livejq('body').append(this.iFrameDraggable);
            this.liveEditPageProxy.createDraggable(this.iFrameDraggable);
            this.iFrameDraggable.simulate('mousedown').hide();

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