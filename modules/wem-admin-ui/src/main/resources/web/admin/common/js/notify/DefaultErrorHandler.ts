module api.notify {

    export class DefaultErrorHandler {

        static handle(error: any) {

            if (api.ObjectHelper.iFrameSafeInstanceOf(error, Error)) {
                console.error(error);
            } else if (api.ObjectHelper.iFrameSafeInstanceOf(error, Exception)) {
                var message = error.getMessage();

                switch (error.getType()){
                case Type.ERROR:
                    console.error(message);
                    showError(message);
                    break;
                case Type.WARNING:
                    console.warn(message);
                    showWarning(message);
                    break;
                case Type.INFO:
                    console.info(message);
                    showFeedback(message);
                    break;
                }
            } else {
                console.error(error);
                showError(error.toString());
            }

        }

    }
}