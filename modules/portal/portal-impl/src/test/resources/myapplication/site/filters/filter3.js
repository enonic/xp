var filterHelper = require('/lib/filter-helper');

exports.responseFilter = function (req, res) {

    res.headers['X-Custom-Header3'] = 'value3';

    filterHelper.addPageContribution(res, 'bodyEnd', "<script src='js/3rd-party-script.js' type='text/javascript'></script>");

    return res;
};
