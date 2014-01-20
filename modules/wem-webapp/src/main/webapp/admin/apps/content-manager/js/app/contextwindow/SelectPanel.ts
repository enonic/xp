module app.contextwindow {
    export class SelectPanel extends api.ui.Panel {

        private searchBox;
        private data:ComponentData[];
        private dataView:api.ui.grid.DataView<ComponentData>;
        private grid:ComponentGrid;
        private contextWindow:ContextWindow;

        constructor(contextWindow:ContextWindow) {
            super("select-panel");
            this.contextWindow = contextWindow;

            this.dataView = new api.ui.grid.DataView<ComponentData>();
            this.grid = new ComponentGrid(this.dataView);

            this.searchBox = new api.ui.TextInput();
            this.searchBox.addClass("search");
            this.searchBox.setPlaceholder("Search");
            this.searchBox.getEl().addEventListener("keyup", (e) => {
                this.grid.updateFilter(this.searchBox.getValue());
            });

            this.appendChild(this.searchBox);
            this.appendChild(this.grid);

            SelectComponentEvent.on((event) => {
                this.getData(event.getComponent().componentType.type);
            });

            // Using jQuery since grid.setOnClick fires event twice, bug in slickgrid
            jQuery(this.getHTMLElement()).on("click", ".grid-row", (event:JQueryEventObject) => {
                var key = jQuery(event.currentTarget).children('div').data("live-edit-key");
                this.contextWindow.getLiveEditWindow().LiveEdit.component.dragdropsort.EmptyComponent.loadComponent(key);
            });
        }

        private getData(componentType:number):void {
            jQuery.ajax({
                url: api.util.getAdminUri("apps/content-manager/js/data/context-window/mock-components.jsp?componentType=" +
                     componentType),
                success: (data:any, textStatus:string, jqXHR:JQueryXHR) => {
                    this.dataView.setItems(ComponentGrid.toSlickData(data));
                }
            });
        }
    }
}