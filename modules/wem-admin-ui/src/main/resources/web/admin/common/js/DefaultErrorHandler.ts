module api {

    export class DefaultErrorHandler {

        static handle(error: any) {

            if (api.ObjectHelper.iFrameSafeInstanceOf(error, Error)) {
                // Rethrowing Error so that we will get a nice stack trace in the console.
                console.error(error);
                throw error;
            }
            else if (api.ObjectHelper.iFrameSafeInstanceOf(error, AccessDeniedException)) {
                var application: api.app.Application = api.app.Application.getApplication();
                var wnd = application.getWindow();
                new api.app.bar.event.ShowAppLauncherEvent(application, true).fire(wnd.parent);
                new api.app.bar.event.ShowAppLauncherEvent(application, true).fire(wnd);
            }
            else if (api.ObjectHelper.iFrameSafeInstanceOf(error, Exception)) {
                var message = error.getMessage();

                switch (error.getType()) {
                case ExceptionType.ERROR:
                    console.error(message);
                    api.notify.showError(message);
                    break;
                case ExceptionType.WARNING:
                    console.warn(message);
                    api.notify.showWarning(message);
                    break;
                case ExceptionType.INFO:
                    console.info(message);
                    api.notify.showFeedback(message);
                    break;
                }
            } else {
                console.error(error);
                api.notify.showError(error.toString());
                throw error;
            }

        }

    }
}