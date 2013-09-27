module api_ui{

    export interface ToggleSlideActions {

        turnOnAction: api_ui.Action;
        turnOffAction: api_ui.Action;

    }

    export class ToggleSlide extends api_dom.DivEl {

        private actions:ToggleSlideActions;

        private isOn:boolean;

        private slider:api_dom.Element;
        private holder:api_dom.Element;
        private onLabel:api_dom.Element;
        private offLabel:api_dom.Element;

        private animationDuration:number = 300;
        private sliderOffset:number;
        private slideLeft:api_util.Animation;
        private slideRight:api_util.Animation;

        constructor(actions:ToggleSlideActions, initOn:boolean) {
            super('ToogleSlide', 'toggle-slide');

            this.actions = actions;

            this.createMarkup();
            this.calculateStyles();
            this.setupAnimation();

            if( initOn ) {
                this.slideOn();
            }
            else {
                this.slideOff();
            }
            this.isOn = initOn;

            this.getEl().addEventListener('click', () => {
                this.toggle();
            });
        }

        toggle() {
            this.isOn ? this.turnOff() : this.turnOn();
        }

        turnOn() {
            this.slideOn();
            this.isOn = true;
            this.actions.turnOnAction.execute();
        }

        private slideOn() {
            if (this.slideLeft.isRunning()) {
                this.slideLeft.stop();
            }
            this.slideRight.start();
        }

        turnOff() {
            this.slideOff();
            this.isOn = false;
            this.actions.turnOffAction.execute();
        }

        private slideOff() {
            if (this.slideRight.isRunning()) {
                this.slideRight.stop();
            }
            this.slideLeft.start();
        }

        isTurnedOn():boolean {
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
            api_dom.Body.get().appendChild(this);

            var labelWidth = Math.max(onLabelEl.getWidth(), offLabelEl.getWidth());

            // Increase slider width a bit so it hides seam between labels.
            sliderEl.setWidth((labelWidth + 4) + 'px');

            // calculate distance by which the slider moves
            this.sliderOffset = labelWidth - 4;

            // Adjust labels width to the same value.
            onLabelEl.setWidth(labelWidth + 'px');
            offLabelEl.setWidth(labelWidth + 'px');
        }

        private setupAnimation() {
            this.slideLeft = new api_util.Animation(this.animationDuration);
            this.slideLeft.onStep((progress) => {
                this.slider.getEl().setLeft(this.sliderOffset * (1 - progress) + 'px');
            });

            this.slideRight = new api_util.Animation(this.animationDuration);
            this.slideRight.onStep((progress) => {
                this.slider.getEl().setLeft(this.sliderOffset * progress + 'px');
            });
        }

    }
}