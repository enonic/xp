Ext.define('App.lib.util.Messages', {

    phrasesMap: {},

    statics: {

        setMap: function (phrasesMap) {
            this.phrasesMap = phrasesMap;
        },

        get: function () {
            var args = Array.prototype.slice.call(arguments);
            var key = args[0];
            var phrase = this.phrasesMap[key];

            if (phrase === undefined) {
                return '{' + key + ' NOT TRANSLATED}';
            }

            return this.formatPhrase(phrase, args.slice(1, args.length));
        },

        formatPhrase: function (phrase, args) {
            var formatted = phrase;

            for (var i = 0; i < args.length; i++) {
                var regExp = new RegExp('\\{' + (i) + '\\}', 'g');
                formatted = formatted.replace(regExp, args[i]);
            }

            return formatted;
        }

    }

});