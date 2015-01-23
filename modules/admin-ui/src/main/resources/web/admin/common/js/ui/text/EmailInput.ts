module api.ui.text {

    import InputEl = api.dom.InputEl;
    import StringHelper = api.util.StringHelper;
    import CheckEmailAvailabilityRequest = api.security.CheckEmailAvailabilityRequest;

    export class EmailInput extends api.dom.FormInputEl {

        private input: InputEl;

        private originEmail: string;

        private status: string;
        private checkTimeout: number;

        private userStoreKey: api.security.UserStoreKey;

        private focusListeners: {(event: FocusEvent):void}[] = [];

        private blurListeners: {(event: FocusEvent):void}[] = [];

        constructor() {
            super("div", "email-input");

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
            this.appendChild(this.input);

        }

        getValue(): string {
            return this.input.getValue();
        }

        setValue(value: string): EmailInput {
            this.input.setValue(value);
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

        getName(): string {
            return this.input.getName();
        }

        setName(value: string): EmailInput {
            this.input.setName(value);
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
                    this.updateStatus((available || email === this.originEmail) ? 'available' : 'notavailable');
                }).fail((reason) => {
                    this.updateStatus('error');
                })
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