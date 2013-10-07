module app_contextwindow {
    export class EmulatorPanel extends api_ui.Panel {
        private grid:EmulatorGrid;

        constructor() {
            super("EmulatorPanel");

            var text = new api_dom.PEl();
            text.getEl().setInnerHtml("Emulate different client's physical sizes");
            this.appendChild(text);

            this.grid = new EmulatorGrid();
            this.appendChild(this.grid);

            this.getData();
        }

        private getData():void {
            jQuery.ajax({
                url: "/admin/apps/content-manager/js/data/context-window/devices.json",
                success: (data:any, textStatus:string, jqXHR:JQueryXHR) => {
                    this.grid.updateData(EmulatorGrid.toSlickData(data));
                }
            });
        }
    }
}