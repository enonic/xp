module api.ui {

    export interface ToggleSlideActions {

        turnOnAction: api.ui.Action;
        turnOffAction: api.ui.Action;

    }

    export class ToggleSlide extends api.dom.DivEl {

        private actions: ToggleSlideActions;

        private isOn: boolean;
        private enabled: boolean = true;

        private slider: api.dom.Element;
        private holder: api.dom.Element;
        private onLabel: api.dom.Element;
        private offLabel: api.dom.Element;

        private animationDuration: number = 300;
        private sliderOffset: number;
        private slideLeft: api.util.Animation;
        private slideRight: api.util.Animation;

        private disabledClass: string = "disabled";

        constructor(actions: ToggleSlideActions) {
            super('toggle-slide');

            this.actions = actions;
            actions.turnOnAction.onPropertyChanged((action: api.ui.Action)=> {
                this.setEnabled(action.isEnabled());
                this.setVisible(action.isVisible());
            });

            if (!actions.turnOnAction.isEnabled()) {
                this.setEnabled(false);
            }

            this.createMarkup();
            this.calculateStyles();
            this.setupAnimation();

            var turnOn = actions.turnOnAction.isEnabled();
            if (turnOn) {
                this.slideOn();
            }
            else {
                this.slideOff();
            }
            this.isOn = turnOn;

            this.onClicked((event: MouseEvent) => {
                if (this.enabled) {
                    this.toggle();
                }
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

        isTurnedOn(): boolean {
            return this.isOn;
        }

        setEnabled(enabled: boolean) {
            if (enabled != this.enabled) {
                if (!enabled) {
                    this.addClass(this.disabledClass);
                } else {
                    this.removeClass(this.disabledClass);
                }
                this.enabled = enabled;
            }
        }

        isEnabled(): boolean {
            return this.enabled;
        }

        private createMarkup() {
            this.slider = new api.dom.DivEl('slider');
            this.holder = new api.dom.DivEl('holder');
            this.onLabel = new api.dom.DivEl('on');
            this.offLabel = new api.dom.DivEl('off');

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
            api.dom.Body.get().appendChild(this);

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
            this.slideLeft = new api.util.Animation(this.animationDuration);
            this.slideLeft.onStep((progress) => {
                this.slider.getEl().setLeft(this.sliderOffset * (1 - progress) + 'px');
            });

            this.slideRight = new api.util.Animation(this.animationDuration);
            this.slideRight.onStep((progress) => {
                this.slider.getEl().setLeft(this.sliderOffset * progress + 'px');
            });
        }

    }
}