var i18n = (function(bundle){
    const messages = bundle;

    return function(key, element) {
        const message = '#' + key + '#';

        if ((messages != null) && (messages[key] != null)) {
            return messages[key];
        }

        return message;
    };
})(CONFIG.messages);
