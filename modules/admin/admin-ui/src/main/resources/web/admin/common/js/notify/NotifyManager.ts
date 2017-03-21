module api.notify {

    export class NotifyManager {

        private static instance: NotifyManager;

        private notificationLimit: number = 3;

        private queue: NotificationMessage[] = [];

        private lifetime: number = 5000;

        private slideDuration: number = 500;

        private timers: Object = {};

        private el: NotificationContainer;

        private registry: Object = {};

        constructor() {

            this.el = new NotificationContainer();
            api.dom.Body.get().appendChild(this.el);

            this.el.getEl().setBottomPx(0);
        }

        showFeedback(message: string, autoHide: boolean = true) {
            let feedback = Message.newInfo(message, autoHide);
            this.notify(feedback);
        }

        showSuccess(message: string, autoHide: boolean = true) {
            let feedback = Message.newSuccess(message, autoHide);
            this.notify(feedback);
        }

        showError(message: string, autoHide: boolean = true) {
            let error = Message.newError(message, autoHide);
            this.notify(error);
        }

        showWarning(message: string, autoHide: boolean = true) {
            let warning = Message.newWarning(message, autoHide);
            this.notify(warning);
        }

        notify(message: Message) {
            const opts = NotifyOpts.buildOpts(message);

            const limitReached = this.queue.length > 0
                                 || this.el.getWrapper().getChildren().length >= this.notificationLimit;
            if (limitReached) {
                this.queue.push(this.createNotification(opts));
            } else {
                this.renderNotification(this.createNotification(opts));
            }
        }

        private createNotification(opts: NotifyOpts): NotificationMessage {
            const notificationEl = new NotificationMessage(opts.message, opts.autoHide);
            if (opts.type) {
                notificationEl.addClass(opts.type);
            }

            this.registry[notificationEl.getEl().getId()] = notificationEl;
            this.setListeners(notificationEl, opts);

            return notificationEl;
        }

        private renderNotification(notification: NotificationMessage): NotificationMessage {
            this.el.getWrapper().appendChild(notification);
            notification.hide();

            wemjq(notification.getHTMLElement()).animate({
                    height: 'toggle'
                },
                this.slideDuration,
                () => {
                    if (notification.isAutoHide()) {
                        this.timers[notification.getEl().getId()] = {
                            remainingTime: this.lifetime
                        };

                        this.startTimer(notification);
                    }
                });

            return notification;
        }

        hide(messageId: string) {
            if (this.registry[messageId]) {
                this.remove(this.registry[messageId]);
                delete this.registry[messageId];
            }
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
                this.startTimer(el);
            });

            if (opts.listeners) {
                opts.listeners.forEach((listener)=> {
                    el.onClicked(listener);
                });
            }

            el.onRemoved(() => this.handleNotificationRemoved());
        }

        private handleNotificationRemoved() {
            if (this.queue.length > 0) {
                const notification = this.queue.shift();
                this.renderNotification(notification);
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
            let timer = this.timers[el.getEl().getId()];

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
            let timer = this.timers[el.getEl().getId()];

            if (!timer || !timer.id) {
                return;
            }

            clearTimeout(timer.id);
            timer.id = null;
            timer.remainingTime -= Date.now() - timer.startTime;
        }

        static get(): NotifyManager {

            if (window != window.parent) {

                return this.getFromParentIFrame();
            }

            if (!NotifyManager.instance) {
                NotifyManager.instance = new NotifyManager();
            }

            return NotifyManager.instance;
        }

        private static getFromParentIFrame(): NotifyManager {
            let context = window;
            while (context != window.parent) {
                context = window.parent;
            }

            return context['api']['notify']['NotifyManager'].get();
        }
    }

}
