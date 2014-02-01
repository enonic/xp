module app.contextwindow {
    export interface ComponentData {
        title:string;
        subtitle:string;
        iconUrl:string;
    }

    export interface ComponentTypesPanelConfig {

        contextWindow: ContextWindow;
        liveEditIFrame: api.dom.IFrameEl;
        liveEditWindow: any;
        liveEditJQuery: JQueryStatic;
        draggingMask: api.ui.DraggingMask;
    }

    export class ComponentTypesPanel extends api.ui.Panel {

        private searchBox;
        private dataView:api.ui.grid.DataView<ComponentData>;
        private data:ComponentData[];
        private grid:ComponentGrid;
        private contextWindow:ContextWindow;
        private liveEditIFrame: api.dom.IFrameEl;
        private liveEditWindow: any;
        private liveEditJQuery: JQueryStatic;
        private draggingMask: api.ui.DraggingMask;

        constructor(config:ComponentTypesPanelConfig) {
            super();
            this.addClass('component-types-panel');

            this.contextWindow = config.contextWindow;
            this.liveEditIFrame = config.liveEditIFrame;
            this.liveEditWindow = config.liveEditWindow;
            this.liveEditJQuery = config.liveEditJQuery;
            this.draggingMask = config.draggingMask;

            this.dataView = new api.ui.grid.DataView<ComponentData>();

            this.grid = new ComponentGrid(this.dataView, {draggableRows: true, rowClass: "comp"});

            this.searchBox = new api.ui.TextInput();
            this.searchBox.addClass("search");
            this.searchBox.setPlaceholder("Search");
            this.searchBox.getEl().addEventListener("keyup", (e) => {
                this.grid.updateFilter(this.searchBox.getValue());
            });

            this.appendChild(this.searchBox);
            this.appendChild(this.grid);
            this.getData();
        }

        afterRender() {
            this.initComponentDraggables();
        }

        setData(dataArray:ComponentData[]) {
            this.data = dataArray;
        }

        appendData(data:ComponentData) {
            this.data.push(data);
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
                    this.contextWindow.show();
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
                (event:JQueryEventObject) => {
                    jQuery('[data-context-window-draggable="true"]').simulate('mouseup');
                    this.contextWindow.show();
                });
        }

        onStartDrag(event, ui) {
            this.draggingMask.show();
        }

        onDragOverIFrame(event, ui) {

            this.draggingMask.hide();

            var clone = this.liveEditJQuery(ui.draggable.clone());

            clone.css({
                'position': 'absolute',
                'z-index': '5100000',
                'top': '-1000px'
            });

            console.log(clone);
            this.liveEditJQuery('body').append(clone);

            ui.helper.hide(null);

            this.liveEditWindow.LiveEdit.component.dragdropsort.DragDropSort.createJQueryUiDraggable(clone);

            clone.simulate('mousedown');

            this.contextWindow.hide();
        }

        private getData():void {
            jQuery.ajax({
                url: api.util.getAdminUri("apps/content-manager/js/data/context-window/mock-component-types.json"),
                success: (data:any, textStatus:string, jqXHR:JQueryXHR) => {
                    this.dataView.setItems(ComponentGrid.toSlickData(data));
                }
            });
        }
    }
}