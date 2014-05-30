module app.wizard.page.contextwindow.insert {

    export interface ComponentTypesPanelConfig {

        liveEditPage: app.wizard.page.LiveEditPageProxy;
    }

    export class InsertablesPanel extends api.ui.Panel {

        private liveEditPage: app.wizard.page.LiveEditPageProxy;

        private insertablesGrid: InsertablesGrid;

        private insertablesDataView: api.ui.grid.DataView<Insertable>;

        private hideContextWindowRequestListeners: {(): void;}[] = [];

        constructor(config: ComponentTypesPanelConfig) {
            super("insertables-panel");
            this.liveEditPage = config.liveEditPage;

            var topDescription = new api.dom.PEl();
            topDescription.getEl().setInnerHtml('Drag and drop components into the page');
            this.appendChild(topDescription);

            this.insertablesDataView = new api.ui.grid.DataView<Insertable>();
            this.insertablesGrid = new InsertablesGrid(this.insertablesDataView, {draggableRows: true, rowClass: "comp"});

            this.appendChild(this.insertablesGrid);
            this.insertablesDataView.setItems(Insertables.ALL, "name");

            this.onRendered((event) => {
                this.initComponentDraggables();
            })
        }

        initComponentDraggables() {
            var components = wemjq('[data-context-window-draggable="true"]');

            components.draggable({
                zIndex: 400000,
                cursorAt: {left: -10, top: -15},
                appendTo: 'body',
                cursor: 'move',
                revert: 'true',
                distance: 10,
                addClasses: false,
                helper: () => {
                    return wemjq('<div id="live-edit-drag-helper" class="live-edit-font-icon-drop-not-allowed" style="width: 48px; height: 48px; position: absolute; z-index: 400000;" data-live-edit-drop-allowed="false"></div>');
                },
                scope: 'component',
                start: (event:Event, ui:JQueryUI.DroppableEventUIParam) => {
                    this.onStartDrag(event, ui);
                },
                stop: () => {

                }
            });

            wemjq(this.liveEditPage.getIFrame().getHTMLElement()).droppable({
                tolerance: 'pointer',
                addClasses: false,
                accept: '.comp',
                scope: 'component',
                over: (event:Event, ui:JQueryUI.DroppableEventUIParam) => {
                    this.onDragOverIFrame(event, ui);
                }
            });

            this.liveEditPage.onSortableStop(() => {
                this.simulateMouseUpForDraggable();
            });
            this.liveEditPage.onSortableUpdate(() => {
                this.simulateMouseUpForDraggable();
            });
            this.liveEditPage.onDraggableStop(() => {
                this.simulateMouseUpForDraggable();
            });
        }

        private simulateMouseUpForDraggable() {
            wemjq('[data-context-window-draggable="true"]').simulate('mouseup');
        }

        private onStartDrag(event:Event, ui:JQueryUI.DroppableEventUIParam) {
            this.liveEditPage.showDragMask();
        }

        private onDragOverIFrame(event:Event, ui:JQueryUI.DroppableEventUIParam) {

            this.liveEditPage.hideDragMask();

            var liveEditJQuery = this.liveEditPage.getLiveEditJQuery();
            var clonedDragable = liveEditJQuery(ui.draggable.clone());

            clonedDragable.css({
                'position': 'absolute',
                'z-index': '5100000',
                'top': '-1000px'
            });

            liveEditJQuery('body').append(clonedDragable);

            ui.helper.hide(null);

            this.liveEditPage.createDraggable(clonedDragable);

            clonedDragable.simulate('mousedown');

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