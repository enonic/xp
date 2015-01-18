module app.wizard.page.contextwindow.insert {

    export interface ComponentTypesPanelConfig {

        liveEditPage: app.wizard.page.LiveEditPageProxy;
    }

    export class InsertablesPanel extends api.ui.panel.Panel {

        private liveEditPage: app.wizard.page.LiveEditPageProxy;

        private insertablesGrid: InsertablesGrid;

        private insertablesDataView: api.ui.grid.DataView<Insertable>;

        private hideContextWindowRequestListeners: {(): void;}[] = [];

        private draggable: any;

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
            });

            this.onShown(this.updateDraggables.bind(this));
        }

        initComponentDraggables() {
            wemjq(this.liveEditPage.getIFrame().getHTMLElement()).droppable({
                tolerance: 'pointer',
                addClasses: false,
                accept: '.comp',
                scope: 'component',
                over: (event: Event, ui: JQueryUI.DroppableEventUIParam) => {
                    this.onDragOverIFrame(event, ui);
                }
            });

            this.liveEditPage.onItemFromContextWindowDropped(() => {
                this.simulateMouseUpForDraggable();
            });
        }

        private updateDraggables() {
            var components = wemjq('[data-context-window-draggable="true"]:not(.ui-draggable)');

            components.draggable({
                cursorAt: {left: -10, top: -15},
                appendTo: 'body',
                cursor: 'move',
                revert: 'true',
                distance: 10,
                scope: 'component',
                helper: () => api.ui.DragHelper.getHtml(),
                start: (event: Event, ui: JQueryUI.DroppableEventUIParam) => this.handleDragStart(event, ui)
            });
        }

        private simulateMouseUpForDraggable() {
            wemjq('[data-context-window-draggable="true"]').simulate('mouseup');
        }

        private handleDragStart(event: Event, ui: JQueryUI.DroppableEventUIParam) {
            this.liveEditPage.showDragMask();
        }

        private onDragOverIFrame(event: Event, ui: JQueryUI.DroppableEventUIParam) {

            this.liveEditPage.hideDragMask();

            var liveEditJQuery = this.liveEditPage.getLiveEditJQuery();
            var clonedDraggable = liveEditJQuery(ui.draggable.clone());

            clonedDraggable.css({
                'position': 'absolute',
                'top': '-1000px'
            });

            liveEditJQuery('body').append(clonedDraggable);

            ui.helper.hide(null);

            this.liveEditPage.createDraggable(clonedDraggable);

            clonedDraggable.simulate('mousedown');

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