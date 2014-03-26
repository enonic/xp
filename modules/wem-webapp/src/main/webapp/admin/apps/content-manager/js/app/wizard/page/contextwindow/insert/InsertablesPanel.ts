module app.wizard.page.contextwindow.insert {

    export interface ComponentTypesPanelConfig {

        contextWindow: ContextWindow;
        liveEditIFrame: api.dom.IFrameEl;
        liveEditWindow: any;
        liveEditJQuery: JQueryStatic;
        draggingMask: api.ui.DragMask;
    }

    export class InsertablesPanel extends api.ui.Panel {

        private contextWindow: ContextWindow;
        private liveEditIFrame: api.dom.IFrameEl;
        private liveEditWindow: any;
        private liveEditJQuery: JQueryStatic;
        private draggingMask: api.ui.DragMask;

        private insertablesGrid: InsertablesGrid;
        private insertablesDataView: api.ui.grid.DataView<Insertable>;

        constructor(config: ComponentTypesPanelConfig) {
            super("insertables-panel");

            this.contextWindow = config.contextWindow;
            this.liveEditIFrame = config.liveEditIFrame;
            this.liveEditWindow = config.liveEditWindow;
            this.liveEditJQuery = config.liveEditJQuery;
            this.draggingMask = config.draggingMask;

            this.insertablesDataView = new api.ui.grid.DataView<Insertable>();
            this.insertablesGrid = new InsertablesGrid(this.insertablesDataView, {draggableRows: true, rowClass: "comp"});

            this.appendChild(this.insertablesGrid);
            this.insertablesDataView.setItems(Insertables.ALL, "name");

            this.onRendered((event) => {
                this.initComponentDraggables();
            })
        }

        initComponentDraggables() {
            var components = jQuery('[data-context-window-draggable="true"]');

            components.liveDraggable({
                zIndex: 400000,
                cursorAt: {left: -10, top: -15},
                appendTo: 'body',
                cursor: 'move',
                revert: 'true',
                distance: 10,
                addClasses: false,
                helper: 'clone',
                scope: 'component',
                start: (event, ui) => {
                    this.onStartDrag(event, ui);
                },
                stop: () => {

                }
            });

            jQuery(this.liveEditIFrame.getHTMLElement()).droppable({
                tolerance: 'pointer',
                addClasses: false,
                accept: '.comp',
                scope: 'component',
                over: (event, ui) => {
                    this.onDragOverIFrame(event, ui);
                }
            });

            this.liveEditJQuery(this.liveEditWindow).on('sortableUpdate.liveEdit sortableStop.liveEdit draggableStop.liveEdit',
                (event: JQueryEventObject) => {
                    jQuery('[data-context-window-draggable="true"]').simulate('mouseup');
                });
        }

        onStartDrag(event, ui) {
            //this.draggingMask.show();
        }

        onDragOverIFrame(event, ui) {

            //this.draggingMask.hide();

            var clone = this.liveEditJQuery(ui.draggable.clone());

            clone.css({
                'position': 'absolute',
                'z-index': '5100000',
                'top': '-1000px'
            });

            this.liveEditJQuery('body').append(clone);

            ui.helper.hide(null);

            this.liveEditWindow.LiveEdit.component.dragdropsort.DragDropSort.createJQueryUiDraggable(clone);

            clone.simulate('mousedown');

            this.contextWindow.hide();
        }
    }
}