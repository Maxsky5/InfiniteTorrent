'use strict';

angular.module('infinitetorrentApp')
    .factory('Torrent', function ($resource, DateUtils) {
        console.log('torrent.service.js');
        console.log($resource);
        return $resource('api/torrents/:id', {}, {
            query: { method: 'GET', isArray: true},
            get: {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    data.created = DateUtils.convertDateTimeFromServer(data.created);
                    return data;
                }
            },
            update: { method:'PUT' },
            download: { method:'GET', url: 'api/torrents/download/:id' }
        });
    });
