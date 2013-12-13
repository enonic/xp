/**
 * This napespace provides a familiar console object for logging and debugging.
 *
 * @namespace console
 */
var console = function () {

    function format(args) {
        var msg = args[0] ? String(args[0]) : "";
        var pattern = /%[sdifo]/;
        for (var i = 1; i < args.length; i++) {
            msg = pattern.test(msg)
                ? msg.replace(pattern, String(args[i]))
                : msg + " " + args[i];
        }
        return msg;
    }

    return {

        /**
         * Logs a message to the console.
         *
         * The first argument to log may be a string containing printf-like placeholders.
         * Otherwise, multipel arguments will be concatenated separated by spaces.

         * @param msg... one or more message arguments
         */
        log: function (msg) {
            return format(arguments);
        }

    }

}();
