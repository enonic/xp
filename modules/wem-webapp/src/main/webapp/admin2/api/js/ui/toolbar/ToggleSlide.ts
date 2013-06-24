module api_ui_toolbar {

    export interface ToggleSlideActions {

        turnOnAction: api_ui.Action;
        turnOffAction: api_ui.Action;

    }

    export class ToggleSlide extends api_dom.DivEl {

        private actions:ToggleSlideActions;

        private isOn:bool;

        private slider:api_dom.Element;
        private holder:api_dom.Element;
        private onLabel:api_dom.Element;
        private offLabel:api_dom.Element;

        private animationDuration:number = 300;
        private slideLeft:api_util.Animation;
        private slideRight:api_util.Animation;

        constructor(actions:ToggleSlideActions, initOn:bool) {
            super('ToogleSlide', 'toggle-slide');

            this.actions = actions;

            this.createMarkup();
            this.calculateStyles();
            this.setupAnimation();

            initOn ? this.turnOn() : this.turnOff();

            this.getEl().addEventListener('click', () => {
                this.toggle();
            });
        }

        toggle() {
            this.isOn ? this.turnOff() : this.turnOn();
        }

        turnOn() {
            if (this.slideLeft.isRunning()) {
                this.slideLeft.stop();
            }
            this.slideRight.start();

            this.isOn = true;

            this.actions.turnOnAction.execute();
        }

        turnOff() {
            if (this.slideRight.isRunning()) {
                this.slideRight.stop();
            }
            this.slideLeft.start();

            this.isOn = false;

            this.actions.turnOffAction.execute();
        }

        isTurnedOn():bool {
            return this.isOn;
        }

        private createMarkup() {
            this.slider = new api_dom.DivEl(null, 'slider');
            this.holder = new api_dom.DivEl(null, 'holder');
            this.onLabel = new api_dom.DivEl(null, 'on');
            this.offLabel = new api_dom.DivEl(null, 'off');

            this.appendChild(this.slider);
            this.appendChild(this.holder);
            this.holder.appendChild(this.onLabel);
            this.holder.appendChild(this.offLabel);

            this.onLabel.getEl().setInnerHtml(this.actions.turnOnAction.getLabel());
            this.offLabel.getEl().setInnerHtml(this.actions.turnOffAction.getLabel());
        }

        private calculateStyles() {
            var sliderEl = this.slider.getEl(),
                onLabelEl = this.onLabel.getEl(),
                offLabelEl = this.offLabel.getEl();

            // ToggleSlide width depends on width of longest label.
            // To have labels width calculated by browser they should be rendered into dom.
            // Therefore append ToggleSlide to body.
            // It will be removed from here when it is inserted in another place.
            new api_dom.ElementHelper(document.body).appendChild(this.getHTMLElement());

            var labelWidth = Math.max(onLabelEl.getWidth(), offLabelEl.getWidth());

            // Increase slider width a bit so it hides seam between labels.
            sliderEl.setWidth((labelWidth + 4) + 'px');

            // Adjust labels width to the same value.
            onLabelEl.setWidth(labelWidth + 'px');
            offLabelEl.setWidth(labelWidth + 'px');
        }

        private setupAnimation() {
            // calculate distance by which the slider moves
            var offset = this.getEl().getWidth() - this.slider.getEl().getWidth();

            this.slideLeft = new api_util.Animation(this.animationDuration);
            this.slideLeft.onStep((progress) => {
                this.slider.getEl().setLeft(offset * (1 - progress) + 'px');
            });

            this.slideRight = new api_util.Animation(this.animationDuration);
            this.slideRight.onStep((progress) => {
                this.slider.getEl().setLeft(offset * progress + 'px');
            });
        }

    }
}