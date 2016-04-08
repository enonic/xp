module api.ui.text {

    import InputEl = api.dom.InputEl;
    import StringHelper = api.util.StringHelper;
    import CheckEmailAvailabilityRequest = api.security.CheckEmailAvailabilityRequest;

    export class EmailInput extends api.dom.CompositeFormInputEl {

        private input: InputEl;

        private originEmail: string;

        private status: string;

        private checkTimeout: number;

        private userStoreKey: api.security.UserStoreKey;

        private focusListeners: {(event: FocusEvent):void}[];

        private blurListeners: {(event: FocusEvent):void}[];

        constructor() {
            this.focusListeners = [];
            this.blurListeners = [];
            this.input = new InputEl(undefined, 'email');
            this.input.setPattern("^[a-zA-Z0-9\_\-\.]+@[a-zA-Z0-9\-]+(?:\.[a-zA-Z0-9\-]{2,})*(?:\.[a-zA-Z]{2,4})$");

            this.input.onFocus((event: FocusEvent) => {
                this.notifyFocused(event);
            });

            this.input.onBlur((event: FocusEvent) => {
                this.notifyBlurred(event);
            });
            this.input.onInput((event: Event) => {
                if (this.checkTimeout) {
                    clearTimeout(this.checkTimeout);
                }

                this.checkTimeout = setTimeout((email) => this.checkAvailability(email), 500, this.input.getValue());

            });

            super(this.input);
            this.addClass("email-input just-shown");
            this.updateStatus('available');
        }

        getInput(): InputEl {
            return this.input;
        }

        doSetValue(value: string, silent?: boolean): EmailInput {
            super.doSetValue(value, silent);
            this.checkAvailability(value);
            return this;
        }

        getOriginEmail(): string {
            return this.originEmail;
        }

        setOriginEmail(value: string): EmailInput {
            this.originEmail = value;
            return this;
        }

        setUserStoreKey(userStoreKey: api.security.UserStoreKey): EmailInput {
            this.userStoreKey = userStoreKey;
            return this;
        }

        isAvailable(): boolean {
            return this.hasClass('available');
        }

        private checkAvailability(email: string) {
            var status;
            var isValid = this.input.isValid();
            this.toggleClass('invalid', !isValid);

            if (!StringHelper.isEmpty(email) && isValid) {
                status = 'checking';

                new CheckEmailAvailabilityRequest(email).setUserStoreKey(this.userStoreKey).sendAndParse().then((available: boolean) => {
                    var availability = available || email === this.originEmail;
                    this.updateStatus(availability ? 'available' : 'notavailable');
                    this.notifyValidityChanged(isValid && availability);
                    this.removeClass("just-shown");
                }).fail((reason) => {
                    this.notifyValidityChanged(false);
                    this.updateStatus('error');
                }).done();
            } else {
                this.notifyValidityChanged(isValid);
            }

            this.updateStatus(status);
        }

        private updateStatus(status?: string) {
            if (!!this.status) {
                this.removeClass(this.status);
            }
            if (!StringHelper.isEmpty(status)) {
                this.status = status;
                this.addClass(this.status);
            }
        }

        isValid(): boolean {
            return this.input.isValid() && !StringHelper.isEmpty(this.doGetValue()) && this.isAvailable();
        }

        validate(): boolean {
            return this.input.validate();
        }


        onFocus(listener: (event: FocusEvent) => void) {
            this.focusListeners.push(listener);
        }

        unFocus(listener: (event: FocusEvent) => void) {
            this.focusListeners = this.focusListeners.filter((curr) => {
                return curr !== listener;
            });
        }

        onBlur(listener: (event: FocusEvent) => void) {
            this.blurListeners.push(listener);
        }

        unBlur(listener: (event: FocusEvent) => void) {
            this.blurListeners = this.blurListeners.filter((curr) => {
                return curr !== listener;
            });
        }

        private notifyFocused(event: FocusEvent) {
            this.focusListeners.forEach((listener) => {
                listener(event);
            })
        }

        private notifyBlurred(event: FocusEvent) {
            this.blurListeners.forEach((listener) => {
                listener(event);
            })
        }
    }
}