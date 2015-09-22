module app.wizard {
    export class PublishAction extends api.ui.Action {

        private static BASE_STRING: string = "Publish now";

        constructor() {
            super(PublishAction.BASE_STRING);
        }

        setToBePublishedAmount(amount: number) {
            this.setEnabled(amount >= 1)
            this.setLabel(PublishAction.BASE_STRING + " (" + amount + ")")
        }
    }
}