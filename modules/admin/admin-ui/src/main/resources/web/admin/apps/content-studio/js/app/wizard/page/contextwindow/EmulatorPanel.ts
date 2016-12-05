import "../../../../api.ts";
import {LiveEditPageProxy} from "../LiveEditPageProxy";
import {EmulatorGrid} from "./EmulatorGrid";

export interface EmulatorPanelConfig {

    liveEditPage: LiveEditPageProxy;
}

export class EmulatorPanel extends api.ui.panel.Panel {

    private dataView: api.ui.grid.DataView<any>;
    private grid: EmulatorGrid;

    private liveEditPage: LiveEditPageProxy;

    constructor(config: EmulatorPanelConfig) {
        super("emulator-panel");

        this.liveEditPage = config.liveEditPage;

        var text = new api.dom.PEl();
        text.getEl().setInnerHtml("Emulate different client's physical sizes");
        this.appendChild(text);

        this.dataView = new api.ui.grid.DataView<any>();
        this.grid = new EmulatorGrid(this.dataView);
        this.appendChild(this.grid);

        this.getData();

        // Using jQuery since grid.setOnClick fires event twice, bug in slickgrid
        wemjq(this.getHTMLElement()).on("click", ".grid-row > div", (event: JQueryEventObject) => {

            var el = wemjq(event.currentTarget),
                width = el.data("width"),
                height = el.data("height"),
                units = el.data("units");

            this.liveEditPage.setWidth(width + units);
            this.liveEditPage.setHeight(height + units);

            if (units === "px") {
                this.updateLiveEditFrameContainerHeight(height);
            }

        });

        wemjq(this.getHTMLElement()).on("click", ".rotate", (event: JQueryEventObject) => {

            event.stopPropagation();

            this.liveEditPage.setHeightPx(this.liveEditPage.getHeight());
            this.liveEditPage.setWidthPx(this.liveEditPage.getWidth());
        });
    }

    private getData(): void {
        wemjq.ajax({
            url: api.util.UriHelper.getAdminUri("apps/content-studio/js/data/context-window/devices.json"),
            success: (data: any, textStatus: string, jqXHR: JQueryXHR) => {
                this.dataView.setItems(EmulatorGrid.toSlickData(data));
                this.grid.setActiveCell(0, 0); // select first option
            }
        });
    }

    private updateLiveEditFrameContainerHeight(height: number) { // this helps to put horizontal scrollbar in the bottom of live edit frame
        var body = document.body,
            html = document.documentElement;

        var pageHeight = Math.max(body.scrollHeight, body.offsetHeight,
            html.clientHeight, html.scrollHeight, html.offsetHeight);

        var frameParent = this.liveEditPage.getIFrame().getHTMLElement().parentElement;
        if (height > pageHeight) {
            frameParent.style.height = "";
            frameParent.classList.add("overflow");
        }
        else {
            frameParent.style.height = height + this.getScrollbarWidth() + "px";
            frameParent.classList.remove("overflow");
        }
    }

    private getScrollbarWidth(): number {
        var outer = document.createElement("div");
        outer.style.visibility = "hidden";
        outer.style.width = "100px";
        outer.style.msOverflowStyle = "scrollbar"; // needed for WinJS apps

        document.body.appendChild(outer);

        var widthNoScroll = outer.offsetWidth;
        // force scrollbars
        outer.style.overflow = "scroll";

        // add innerdiv
        var inner = document.createElement("div");
        inner.style.width = "100%";
        outer.appendChild(inner);

        var widthWithScroll = inner.offsetWidth;

        // remove divs
        outer.parentNode.removeChild(outer);

        return widthNoScroll - widthWithScroll;
    }
}
