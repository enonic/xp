module api_ui_toolbar {

    export class ToggleSlide extends api_ui.DivEl {

        private onText:string;
        private offText:string;

        private isOn:bool;

        private thumb:api_ui.Element;
        private holder:api_ui.Element;
        private onLabel:api_ui.Element;
        private offLabel:api_ui.Element;

        private animationId:number;
        private animationDuration:number = 300;

        constructor(onText:string, offText:string, initOn:bool) {
            super('ToogleSlide', 'toggle-slide');

            this.onText = onText;
            this.offText = offText;

            this.createMarkup();
            this.calculateStyles();

            initOn ? this.turnOn() : this.turnOff();

            this.addListeners();
        }

        toggle() {
            this.isOn ? this.turnOff() : this.turnOn();
        }

        turnOn() {
            this.slideRight();
            this.isOn = true;
        }

        turnOff() {
            this.slideLeft();
            this.isOn = false;
        }

        isTurnedOn():bool {
            return this.isOn;
        }

        private createMarkup() {
            this.thumb = new api_ui.DivEl(null, 'thumb');
            this.holder = new api_ui.DivEl(null, 'holder');
            this.onLabel = new api_ui.DivEl(null, 'on');
            this.offLabel = new api_ui.DivEl(null, 'off');

            var thumbEl = this.thumb.getEl(),
                holderEl = this.holder.getEl(),
                onLabelEl = this.onLabel.getEl(),
                offLabelEl = this.offLabel.getEl();

            this.getEl()
                .appendChild(thumbEl.getHTMLElement())
                .appendChild(holderEl.getHTMLElement());
            holderEl
                .appendChild(onLabelEl.getHTMLElement())
                .appendChild(offLabelEl.getHTMLElement());

            onLabelEl.setInnerHtml(this.onText);
            offLabelEl.setInnerHtml(this.offText);
        }

        private calculateStyles() {
            var thumbEl = this.thumb.getEl(),
                onLabelEl = this.onLabel.getEl(),
                offLabelEl = this.offLabel.getEl();

            document.body.appendChild(this.getHTMLElement());

            var onWidth = onLabelEl.getWidth(),
                offWidth = offLabelEl.getWidth();

            var thumbWidth = Math.max(onWidth, offWidth);

            thumbEl.setWidth((thumbWidth + 4) + 'px');
            onLabelEl.setWidth(thumbWidth + 'px');
            offLabelEl.setWidth(thumbWidth + 'px');
        }

        private addListeners() {
            var me = this;

            me.getEl().addEventListener('click', () => {
                me.toggle();
            });
        }

        private slideLeft() {
            var thumbEl = this.thumb.getEl(),
                offset = this.calculateOffset();

            this.animate((progress) => {
                thumbEl.setLeft(offset * (1 - progress) + 'px');
            });
        }

        private slideRight() {
            var thumbEl = this.thumb.getEl(),
                offset = this.calculateOffset();

            this.animate((progress) => {
                thumbEl.setLeft(offset * progress + 'px');
            });
        }

        private calculateOffset() {
            var toggleWidth = this.getEl().getWidth(),
                thumbWidth = this.thumb.getEl().getWidth();

            return toggleWidth - thumbWidth;
        }

        private animate(step:Function) {
            if (this.animationId) {
                api_util.Animation.stop(this.animationId);
            }

            this.animationId = api_util.Animation.start(step, this.animationDuration);
        }

    }

}