module api_notify {

    var space:number = 3;

    var lifetime:number = 5000;

    var slideDuration:number = 1000;

    var templates = {
        manager: new Ext.Template(
            '<div class="admin-notification-container">',
            '   <div class="admin-notification-wrapper"></div>',
            '</div>'
        ),

        notify: new Ext.Template(
            '<div class="admin-notification" style="height: 0; opacity: 0;">',
            '   <div class="admin-notification-inner">',
            '       <a class="admin-notification-remove" href="#">X</a>',
            '       <div class="admin-notification-content">{message}</div>',
            '   </div>',
            '</div>'
        )
    };

    export class NotifyManager {

        private timers:Object = {};

        private el:any;

        constructor() {
            this.render();
        }

        private render() {
            // Create template
            var template = templates.manager;

            // render manager template to document body
            var node = template.append(Ext.getBody(), {});
            this.el = Ext.get(node);

            // align the element (TODO: Should be placed in CSS)
            this.el.setStyle('bottom', 0);
            this.getWrapperEl().setStyle({ margin: 'auto' });
        }

        private getWrapperEl():any {
            return this.el.first('.admin-notification-wrapper');
        }

        notify(message:Message) {
            var opts = buildOpts(message);
            this.doNotify(opts);
        }

        private doNotify(opts:NotifyOpts) {

            var notificationEl = this.renderNotification(opts);
            var height = getInnerEl(notificationEl).getHeight();

            this.setListeners(notificationEl, opts);

            notificationEl.animate({
                duration: slideDuration,
                to: {
                    height: height + space,
                    opacity: 1
                },
                callback: () => {
                    this.timers[notificationEl.id] = {
                        remainingTime: lifetime
                    };

                    this.startTimer(notificationEl);
                }
            });
        }

        private setListeners(el:any, opts:NotifyOpts) {
            el.on({
                'click': {
                    fn: () => {
                        this.remove(el);
                    },
                    // TODO: Click to close?
                    // delegate: '.admin-notification-remove',
                    stopEvent: true
                },
                'mouseover': () => {
                    this.stopTimer(el);
                },
                'mouseleave': () => {
                    this.startTimer(el);
                }
            });

            if (opts.listeners) {
                Ext.each(opts.listeners, (listener) => {
                    el.on({
                        'click': listener
                    });
                });
            }
        }

        private remove(el:any) {
            if (!el) {
                return;
            }

            el.animate({
                duration: slideDuration,
                to: {
                    height: 0,
                    opacity: 0
                },
                callback: () => {
                    Ext.removeNode(el.dom);
                }
            });

            delete this.timers[el.id];
        }

        private startTimer(el:any) {
            var timer = this.timers[el.id];

            if (!timer) {
                return;
            }

            timer.id = setTimeout(() => {
                    this.remove(el);
                },
                timer.remainingTime
            );

            timer.startTime = Date.now();
        }

        private stopTimer(el:any) {
            var timer = this.timers[el.id];

            if (!timer || !timer.id) {
                return;
            }

            clearTimeout(timer.id);
            timer.id = null;
            timer.remainingTime -= Date.now() - timer.startTime;
        }

        private renderNotification(opts:NotifyOpts):any {
            var style = {};

            // create notification DOM element
            var template = templates.notify;
            var notificationEl = template.append(this.getWrapperEl(), opts, true);

            // set notification style
            if (opts.backgroundColor) {
                style['backgroundColor'] = opts.backgroundColor;
            }

            style['marginTop'] = space + 'px';
            getInnerEl(notificationEl).setStyle(style);

            return notificationEl;
        }
    }

    function getInnerEl(notificationEl):any {
        return notificationEl.down('.admin-notification-inner');
    }

    var manager = new NotifyManager();

    export function sendNotification(message:Message) {
        manager.notify(message);
    }
}
