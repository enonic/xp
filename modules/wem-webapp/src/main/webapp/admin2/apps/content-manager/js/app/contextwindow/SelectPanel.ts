module app_contextwindow {
    export class SelectPanel extends api_ui.Panel {

        private searchBox;
        private data:ComponentData[];
        private grid:ComponentGrid;
        private contextWindow:ContextWindow;

        constructor(contextWindow:ContextWindow) {
            super();
            this.contextWindow = contextWindow;
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

            ComponentSelectEvent.on((event) => {
                this.getData();
            });
        }

        private getData():void {
            jQuery.ajax({
                url: "/admin2/apps/content-manager/js/data/context-window/mock-components.jsp?componentType=" +
                     this.contextWindow.getSelectedComponent().type,
                success: (data:any, textStatus:string, jqXHR:JQueryXHR) => {
                    this.grid.updateData(ComponentGrid.toSlickData(data));
                }
            });
        }
    }
}