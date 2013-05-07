module API.notify {

    export class NotifyOpts {
        message:string;
        backgroundColor:string;
        listeners:Object[];
    }

    export function buildOpts(message:Message):NotifyOpts {
        var opts = new NotifyOpts();

        if (message.getType() == Type.ERROR) {
            opts.backgroundColor = 'red';
        } else if (message.getType() == Type.ACTION) {
            opts.backgroundColor = '#669c34';
        }

        createHtmlMessage(message, opts);
        addListeners(message, opts);

        return opts;
    }

    function addListeners(message:Message, opts:NotifyOpts) {
        opts.listeners = [];
        var actions = message.getActions();

        for (var i = 0; i < actions.length; i++) {
            opts.listeners.push({
                fn: actions[i].getHandler(),
                delegate: 'notify_action_' + i,
                stopEvent: true
            });
        }
    }

    function createHtmlMessage(message:Message, opts:NotifyOpts) {
        var actions = message.getActions();
        opts.message = '<span>' + message.getText() + '</span>';

        if (actions.length > 0) {
            var linkHtml = '<span style="float: right; margin-left: 30px;">';

            for (var i = 0; i < actions.length; i++) {
                if ((i > 0) && (i == (actions.length - 1))) {
                    linkHtml += ' or ';
                } else if (i > 0) {
                    linkHtml += ', ';
                }

                linkHtml += '<a href="#" class="notify_action_"' + i + '">';
                linkHtml += actions[i].getName() + "</a>";
            }

            linkHtml += '</span>';
            opts.message = linkHtml + opts.message;
        }
    }
}
