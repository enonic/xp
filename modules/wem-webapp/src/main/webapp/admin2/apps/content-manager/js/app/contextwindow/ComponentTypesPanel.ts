module app_contextwindow {
    export interface ComponentData {
        title:string;
        subtitle:string;
        iconUrl:string;
    }

    export class ComponentTypesPanel extends api_ui.Panel {
        private searchBox;
        private data:ComponentData[];
        private grid:ComponentTypeGrid;
        private contextWindow:ContextWindow;

        constructor(contextWindow:ContextWindow) {
            super();

            this.contextWindow = contextWindow;

            this.data = this.mockData();
            this.grid = new ComponentTypeGrid(this.data);

            this.searchBox = new api_ui.TextInput();
            this.searchBox.setPlaceholder("Search");
            this.searchBox.getEl().addEventListener("keyup", (e) => {
                this.grid.updateFilter(this.searchBox.getValue());
            });

            this.appendChild(this.searchBox);
            this.appendChild(this.grid);
        }

        afterRender() {
            super.afterRender();
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
                revert: 'invalid',
                distance: 10,
                addClasses: false,
                helper: this.createDragHelper,
                start: (event, ui) => {
                    this.onStartDrag(event, ui);
                },
                stop: () => {
                    this.contextWindow.show();
                }
            });

            jQuery(this.contextWindow.getLiveEditEl().getHTMLElement()).droppable({
                tolerance: 'pointer',
                addClasses: false,
                accept: '.comp',
                over: (event, ui) => {
                    console.log("asdf");
                    this.onDragOverIFrame(event, ui);
                }
            });

            this.contextWindow.getLiveEditJQuery()(this.contextWindow.getLiveEditWindow()).on('sortableUpdate.liveEdit sortableStop.liveEdit draggableStop.liveEdit',
                (event:JQueryEventObject) => {
                    jQuery('[data-context-window-draggable="true"]').simulate('mouseup');
                    this.contextWindow.show();
                });
        }

        onStartDrag(event, ui) {
            console.log("showing dragMask")
            this.contextWindow.getDraggingMask().show();
        }

        onDragOverIFrame(event, ui) {
            console.log("On drag over iframe");
            console.log(event, ui);

            var liveEditJQ = this.contextWindow.getLiveEditJQuery();

            this.contextWindow.getDraggingMask().hide();

            var clone = liveEditJQ(ui.draggable.clone());

            clone.css({
                'position': 'absolute',
                'z-index': '5100000',
                'top': '-1000px'
            });

            console.log(clone);
            liveEditJQ('body').append(clone);

            ui.helper.hide(null);

            this.contextWindow.getLiveEditWindow().LiveEdit.component.DragDropSort.createJQueryUiDraggable(clone);

            clone.simulate('mousedown');

            this.contextWindow.hide();
        }

        //TODO: Share with LiveEdit dragHelper
        createDragHelper(jQueryEvent) {
            console.log(jQueryEvent);
            var draggable = jQuery(jQueryEvent.currentTarget),
                text = draggable.data('live-edit-name');

            // fixme: can this be shared with live edit Live Edit/DragDropSort.ts ?
            var html = '<div id="live-edit-drag-helper" style="width: 150px; height: 28px; position: absolute;">' +
                       '    <div id="live-edit-drag-helper-inner">' +
                       '        <div id="live-edit-drag-helper-status-icon" class="live-edit-drag-helper-no"></div>' +
                       '        <span id="live-edit-drag-helper-text" style="width: 134px;">' + text + '</span>' +
                       '    </div>' +
                       '</div>';

            return jQuery(html);
        }

        private mockData():any[] {
            return [
                {
                    "component": {
                        "key": "10517",
                        "type": "image",
                        "name": "Image",
                        "subtitle": "Adds an image to the page"
                    },
                    "id": "10517"
                },
                {
                    "component": {
                        "key": "10017",
                        "type": "layout",
                        "name": "2+1 Column Layout",
                        "subtitle": "The quick, brown fox jumps over a lazy dog"
                    },
                    "id": "10017"
                },
                {
                    "component": {
                        "key": "10016",
                        "type": "layout",
                        "name": "2 Column Layout",
                        "subtitle": "Even the all-powerful Pointing has no control"
                    },
                    "id": "10016"
                },
                {
                    "component": {
                        "key": "10018",
                        "type": "layout",
                        "name": "3 Column Layout",
                        "subtitle": "Far far away, behind the word mountains"
                    },
                    "id": "10018"
                }
            ];
        }
    }
}