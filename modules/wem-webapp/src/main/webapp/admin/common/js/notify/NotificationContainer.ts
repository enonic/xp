module api.notify {

    export class NotificationContainer extends api.dom.DivEl {

        private wrapper: api.dom.DivEl;

        constructor() {
            super("notification-container");
            this.wrapper = new api.dom.DivEl("notification-wrapper");
            this.appendChild(this.wrapper);
        }

        getWrapper(): api.dom.DivEl {
            return this.wrapper;
        }
    }
}