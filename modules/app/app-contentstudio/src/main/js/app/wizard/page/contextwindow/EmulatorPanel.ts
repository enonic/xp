import '../../../../api.ts';
import {LiveEditPageProxy} from '../LiveEditPageProxy';
import {EmulatorGrid} from './EmulatorGrid';
import {EmulatorDevice} from './EmulatorDevice';
import i18n = api.util.i18n;

declare var CONFIG;

export interface EmulatorPanelConfig {

    liveEditPage: LiveEditPageProxy;
}

export class EmulatorPanel extends api.ui.panel.Panel {

    private dataView: api.ui.grid.DataView<any>;
    private grid: EmulatorGrid;

    private liveEditPage: LiveEditPageProxy;

    constructor(config: EmulatorPanelConfig) {
        super('emulator-panel');

        this.liveEditPage = config.liveEditPage;

        let text = new api.dom.PEl();
        text.getEl().setInnerHtml(i18n('field.emulator'));
        this.appendChild(text);

        this.dataView = new api.ui.grid.DataView<any>();
        this.grid = new EmulatorGrid(this.dataView);
        this.appendChild(this.grid);

        this.initData();

        // Using jQuery since grid.setOnClick fires event twice, bug in slickgrid
        wemjq(this.getHTMLElement()).on('click', '.grid-row > div', (event: JQueryEventObject) => {

            let el = wemjq(event.currentTarget);
            let width = el.data('width');
            let height = el.data('height');
            let units = el.data('units');

            this.liveEditPage.setWidth(width + units);
            this.liveEditPage.setHeight(height + units);

            if (units === 'px') {
                this.updateLiveEditFrameContainerHeight(height);
            } else {
                this.resetParentHeight();
            }

        });

        wemjq(this.getHTMLElement()).on('click', '.rotate', (event: JQueryEventObject) => {

            event.stopPropagation();

            this.liveEditPage.setHeightPx(this.liveEditPage.getHeight());
            this.liveEditPage.setWidthPx(this.liveEditPage.getWidth());
        });
    }

    private initData(): void {
        this.dataView.setItems(this.generateEmulatorDevices());
        this.grid.setActiveCell(0, 0); // select first option
    }

    private generateEmulatorDevices(): EmulatorDevice[] {
        const data: EmulatorDevice[] = [];

        const fullSizeDevice: EmulatorDevice = new EmulatorDevice(0, i18n(
            'live.view.device.fullsize'), 'monitor', 100, 100, '%', true, false);
        const smallPhoneDevice: EmulatorDevice = new EmulatorDevice(1, i18n(
            'live.view.device.smallphone'), 'mobile', 320, 480, 'px', false, true);
        const mediumPhoneDevice: EmulatorDevice = new EmulatorDevice(2, i18n(
            'live.view.device.mediumphone'), 'mobile', 375, 667, 'px', false, true);
        const largePhoneDevice: EmulatorDevice = new EmulatorDevice(3, i18n(
            'live.view.device.largephone'), 'mobile', 414, 736, 'px', false, true);
        const tabletDevice: EmulatorDevice = new EmulatorDevice(4, i18n('live.view.device.tablet'), 'tablet', 768, 1024, 'px', false, true);
        const notebook13Device: EmulatorDevice = new EmulatorDevice(5, i18n(
            'live.view.device.notebook13'), 'monitor', 1280, 800, 'px', false, false);
        const notebook15Device: EmulatorDevice = new EmulatorDevice(6, i18n(
            'live.view.device.notebook15'), 'monitor', 1366, 768, 'px', false, false);
        const highDefinitionTVDevice: EmulatorDevice = new EmulatorDevice(7, i18n(
            'live.view.device.highDefinitionTV'), 'monitor', 1920, 1080, 'px', false, false);

        data.push(fullSizeDevice, smallPhoneDevice, mediumPhoneDevice, largePhoneDevice, tabletDevice, notebook13Device, notebook15Device,
            highDefinitionTVDevice);

        return data;
    }

    private updateLiveEditFrameContainerHeight(height: number) { // this helps to put horizontal scrollbar in the bottom of live edit frame
        let body = document.body;
        let html = document.documentElement;

        let pageHeight = Math.max(body.scrollHeight, body.offsetHeight,
            html.clientHeight, html.scrollHeight, html.offsetHeight);

        let frameParent = this.liveEditPage.getIFrame().getHTMLElement().parentElement;
        if (height > pageHeight) {
            frameParent.style.height = '';
            frameParent.classList.add('overflow');
        } else {
            frameParent.style.height = height + this.getScrollbarWidth() + 'px';
            frameParent.classList.remove('overflow');
        }
    }

    private resetParentHeight() {
        let frameParent = this.liveEditPage.getIFrame().getHTMLElement().parentElement;
        frameParent.style.height = '';
        frameParent.classList.remove('overflow');
    }

    private getScrollbarWidth(): number {
        let outer = document.createElement('div');
        outer.style.visibility = 'hidden';
        outer.style.width = '100px';
        outer.style.msOverflowStyle = 'scrollbar'; // needed for WinJS apps

        document.body.appendChild(outer);

        let widthNoScroll = outer.offsetWidth;
        // force scrollbars
        outer.style.overflow = 'scroll';

        // add innerdiv
        let inner = document.createElement('div');
        inner.style.width = '100%';
        outer.appendChild(inner);

        let widthWithScroll = inner.offsetWidth;

        // remove divs
        outer.parentNode.removeChild(outer);

        return widthNoScroll - widthWithScroll;
    }
}
