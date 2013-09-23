module app_contextwindow {
    export interface ComponentData {
        title:string;
        subtitle:string;
        iconUrl:string;
    }

    export class ComponentsPanel extends api_ui.Panel {
        private searchBox;
        private data:ComponentData[];
        private grid:ComponentGrid;

        constructor(contextWindow:ContextWindow) {
            super();
            this.data = this.mockData();
            this.grid = new ComponentGrid(this.data);

            this.searchBox = new api_ui.TextInput();
            this.searchBox.setPlaceholder("Search");
            this.searchBox.getEl().addEventListener("keyup", (e) => {
                this.grid.updateFilter(this.searchBox.getValue());
            });

            this.appendChild(this.searchBox);
            this.appendChild(this.grid);
        }

        setData(dataArray:ComponentData[]) {
            this.data = dataArray;
        }

        appendData(data:ComponentData) {
            this.data.push(data);
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