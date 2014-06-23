module api.notify {

    export class NotifyOpts {
        message: string;
        type: string;
        listeners: {():void}[];
        autoHide: boolean;

        addListeners(message: Message) {
            this.listeners = [];
            var actions = message.getActions();

            for (var i = 0; i < actions.length; i++) {
                /*opts.listeners.push({
                 fn: actions[i].getHandler(),
                 delegate: 'notify.action_' + i,
                 stopEvent: true
                 });*/
                this.listeners.push(actions[i].getHandler())
            }
        }

        createHtmlMessage(message: Message) {
            var actions = message.getActions();
            this.message = '<span>' + message.getText() + '</span>';

            if (actions.length > 0) {
                var linkHtml = '<span style="float: right; margin-left: 30px;">';

                for (var i = 0; i < actions.length; i++) {
                    if ((i > 0) && (i == (actions.length - 1))) {
                        linkHtml += ' or ';
                    } else if (i > 0) {
                        linkHtml += ', ';
                    }

                    linkHtml += '<a href="#" class="notify.action_"' + i + '">';
                    linkHtml += actions[i].getName() + "</a>";
                }

                linkHtml += '</span>';
                this.message = linkHtml + this.message;
            }
        }

        static buildOpts(message: Message): NotifyOpts {
            var opts = new NotifyOpts();
            opts.autoHide = message.getAutoHide();
            if (message.getType() == Type.ERROR) {
                opts.type = 'error';
            }
            else if (message.getType() == Type.WARNING) {
                opts.type = 'warning';
            }
            else if (message.getType() == Type.ACTION) {
                opts.type = 'action';
            }
            else if (message.getType() == Type.SUCCESS) {
                opts.type = 'success';
            }

            opts.createHtmlMessage(message);
            opts.addListeners(message);

            return opts;
        }
    }
}
