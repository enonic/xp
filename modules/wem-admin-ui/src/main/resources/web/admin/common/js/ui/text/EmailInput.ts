module api.ui.text {

    import InputEl = api.dom.InputEl;
    import StringHelper = api.util.StringHelper;
    import CheckEmailAvailabilityRequest = api.security.CheckEmailAvailabilityRequest;

    export class EmailInput extends api.dom.FormInputEl {

        private input: InputEl;

        private status: string;
        private checkTimeout: number;

        constructor() {
            super("div", "email-input");

            this.input = new InputEl(undefined, 'email');
            this.input.setPattern("^[a-zA-Z0-9\_\-\.]+@[a-zA-Z0-9\-]+(?:\.[a-zA-Z0-9\-]{2,})*(?:\.[a-zA-Z]{2,4})$");

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

        getName(): string {
            return this.input.getName();
        }

        setName(value: string): EmailInput {
            this.input.setName(value);
            return this;
        }

        isAvailable(): boolean {
            return this.status === "available";
        }

        private checkAvailability(email: string) {
            var status;
            var isValid = this.input.isValid();
            this.toggleClass('invalid', !isValid);

            if (!StringHelper.isEmpty(email) && isValid) {
                status = 'checking';

                new CheckEmailAvailabilityRequest(email).sendAndParse().then((available: boolean) => {
                    this.updateStatus(available ? 'available' : 'notavailable');
                }).fail((reason) => {
                    this.updateStatus('error');
                })
            }

            this.updateStatus(status);
        }

        private updateStatus(status?: string) {
            if (this.status) {
                this.removeClass(this.status);
            }
            if (!StringHelper.isEmpty(status)) {
                this.status = status;
                this.addClass(this.status);
            }
        }

    }
}