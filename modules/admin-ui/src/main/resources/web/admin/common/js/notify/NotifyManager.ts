module api.notify {

    export class NotifyManager {

        private static instance: NotifyManager;

        private space: number = 3;

        private lifetime: number = 5000;

        private slideDuration: number = 1000;

        private timers: Object = {};

        private el: NotificationContainer;

        private registry: Object = {};

        constructor() {
            this.el = new NotificationContainer();
            api.dom.Body.get().appendChild(this.el);

            this.el.getEl().setBottomPx(0);
        }

        showFeedback(message: string, autoHide: boolean = true): string {
            var feedback = Message.newInfo(message, autoHide);
            return this.notify(feedback);
        }

        showSuccess(message: string, autoHide: boolean = true): string {
            var feedback = Message.newSuccess(message, autoHide);
            return this.notify(feedback);
        }

        showError(message: string, autoHide: boolean = true): string {
            var error = Message.newError(message, autoHide);
            return this.notify(error)
        }

        showWarning(message: string, autoHide: boolean = true): string {
            var warning = Message.newWarning(message, autoHide);
            return this.notify(warning);
        }

        notify(message: Message): string {
            var opts = NotifyOpts.buildOpts(message);
            return this.doNotify(opts);
        }

        hide(messageId: string) {
            if (this.registry[messageId]) {
                this.remove(this.registry[messageId]);
                delete this.registry[messageId];
            }
        }

        private doNotify(opts: NotifyOpts): string {

            var notificationEl = this.renderNotification(opts);
            this.registry[notificationEl.getEl().getId()] = notificationEl;
            this.setListeners(notificationEl, opts);

            wemjq(notificationEl.getHTMLElement()).animate({
                    height: 'toggle'
                },
                this.slideDuration,
                () => {
                    if (opts.autoHide) {
                        this.timers[notificationEl.getEl().getId()] = {
                            remainingTime: this.lifetime
                        };

                        this.startTimer(notificationEl);
                    }
                });
            return notificationEl.getEl().getId();
        }

        private setListeners(el: NotificationMessage, opts: NotifyOpts) {
            el.onClicked(()=> {
                this.remove(el);
                return false;
            });
            el.onMouseEnter(()=> {
                this.stopTimer(el);
            });
            el.onMouseLeave(()=> {
                this.startTimer(el)
            });

            if (opts.listeners) {
                opts.listeners.forEach((listener)=> {
                    el.onClicked(listener);
                });
            }
        }

        private remove(el: NotificationMessage) {
            if (!el) {
                return;
            }

            wemjq(el.getHTMLElement()).animate({
                    height: 'hide'
                }, this.slideDuration, 'linear',
                () => {
                    this.el.getWrapper().removeChild(el);
                });

            delete this.timers[el.getEl().getId()];
        }

        private startTimer(el: NotificationMessage) {
            var timer = this.timers[el.getEl().getId()];

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

        private stopTimer(el: NotificationMessage) {
            var timer = this.timers[el.getEl().getId()];

            if (!timer || !timer.id) {
                return;
            }

            clearTimeout(timer.id);
            timer.id = null;
            timer.remainingTime -= Date.now() - timer.startTime;
        }

        private renderNotification(opts: NotifyOpts): NotificationMessage {
            var style = {};

            // create notification DOM element
            var notifyDiv = new NotificationMessage(opts.message);
            this.el.getWrapper().appendChild(notifyDiv);
            notifyDiv.hide();

            // set notification style
            if (opts.type) {
                notifyDiv.addClass(opts.type);
            }

            return notifyDiv;
        }

        static get(): NotifyManager {
            if (!NotifyManager.instance) {
                NotifyManager.instance = new NotifyManager();
            }

            return NotifyManager.instance;
        }
    }

}
