exports.macro = function (context) {

    return {
        body: 'Macro context: ' + JSON.stringify(context, null, 0),
        pageContributions: {
            headEnd: [
                '<link rel="stylesheet" href="http://enonic.com/css/styles.css" type="text/css" />'
            ],
            bodyEnd: [
                '<script src="http://enonic.com/js/script.js" type="text/javascript"></script>'
            ]
        }
    };

};
