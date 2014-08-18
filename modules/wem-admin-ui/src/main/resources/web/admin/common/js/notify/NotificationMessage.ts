module api.notify {

    export class NotificationMessage extends api.dom.DivEl {

        private notificationInner: api.dom.DivEl;

        constructor(message: string) {
            super("notification");
            this.notificationInner = new api.dom.DivEl("notification-inner");
            var notificationRemove = new api.dom.AEl("notification-remove");
            notificationRemove.setHtml("X");
            notificationRemove.setUrl("#");
            var notificationContent = new api.dom.DivEl("notification-content");
            notificationContent.getEl().setInnerHtml(message);
            this.notificationInner.appendChild(notificationRemove).appendChild(notificationContent);
            this.appendChild(this.notificationInner);
        }

    }
}