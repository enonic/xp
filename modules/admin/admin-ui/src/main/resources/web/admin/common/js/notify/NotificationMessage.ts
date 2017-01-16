module api.notify {

    export class NotificationMessage extends api.dom.DivEl {

        private notificationInner: api.dom.DivEl;

        constructor(message: string) {
            super("notification");
            this.notificationInner = new api.dom.DivEl("notification-inner");
            let notificationRemove = new api.dom.SpanEl("notification-remove");
            notificationRemove.setHtml("X");
            let notificationContent = new api.dom.DivEl("notification-content");
            notificationContent.getEl().setInnerHtml(message, false);
            this.notificationInner.appendChild(notificationRemove).appendChild(notificationContent);
            this.appendChild(this.notificationInner);
        }

    }
}
