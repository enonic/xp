module app_contextwindow {
    export class SelectPanel extends api_ui.Panel {

        private searchBox;
        private data:ComponentData[];
        private grid:ComponentGrid;
        private contextWindow:ContextWindow;

        constructor(contextWindow:ContextWindow) {
            super();

            this.data = this.mockData();
            var onClick = () => {
            };
            this.grid = new ComponentGrid(this.data, {onClick: onClick});

            this.searchBox = new api_ui.TextInput();
            this.searchBox.setPlaceholder("Search");
            this.searchBox.getEl().addEventListener("keyup", (e) => {
                this.grid.updateFilter(this.searchBox.getValue());
            });

            this.appendChild(this.searchBox);
            this.appendChild(this.grid);
        }

        private mockData():any[] {
            return [
                {
                    "id": 10517,
                    "component": {
                        "key": "10517",
                        "type": 2,
                        "typeName": "layout",
                        "name": "Layout",
                        "subtitle": "Adds a layout component to the page"
                    }
                },
                {
                    "id": 10617,
                    "component": {
                        "key": "10617",
                        "type": 3,
                        "typeName": "part",
                        "name": "Part",
                        "subtitle": "Adds a part component to the page"
                    }
                },
                {
                    "id": 10717,
                    "component": {
                        "key": "10717",
                        "type": 4,
                        "typeName": "image",
                        "name": "Image",
                        "subtitle": "Adds an image component to the page"
                    }
                },
                {
                    "id": 10817,
                    "component": {
                        "key": "10817",
                        "type": 5,
                        "typeName": "paragraph",
                        "name": "Paragraph",
                        "subtitle": "Adds a paragraph component to the page"
                    }
                }
            ];
        }
    }
}