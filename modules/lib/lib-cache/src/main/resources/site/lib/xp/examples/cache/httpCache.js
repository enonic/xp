// BEGIN
// Caching HTTP requests
var cacheLib = require('/lib/xp/cache');
var httpClient = require('/lib/xp/http-client');

// Initialize the cache outside the exported functions, otherwise it will be reset for every page load
var ipToCountryCache = cacheLib.newCache({
    size: 100,      // number of items kept in cache
    expire: 60 * 60 // time to live: 1 hour (in seconds)
});

exports.get = function (req) {
    var ip = req.remoteAddress;

    // With cache.get(key, callback_function) we indicate the name/key to look up in the cache
    //  if it exists we fetch it from cache
    //  otherwise we call the function and store its result in the cache
    var location = ipToCountryCache.get(ip, function () {
        log.info('Not in cache - requesting to external service');

        var response = httpClient.request({
            url: 'http://freegeoip.net/json/' + ip
        });
        return JSON.parse(response.body);
    });

    log.info('Request from IP ' + ip + ' => Country: ' + location.country_name);
};
// END
