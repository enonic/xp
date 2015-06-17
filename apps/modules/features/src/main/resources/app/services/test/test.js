function handleGet(req) {

    var site = execute('portal.getSite');
    var siteJson = JSON.stringify(site, null, 4);
    log.info('Site: \r\n %s', siteJson);

    return {
        contentType: 'text/html',
        body: "<h2>Site JSON</h2> <code>" + siteJson + "</code>"
    };
}

exports.get = handleGet;
