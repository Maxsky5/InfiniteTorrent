'use strict';

angular.module('infinitetorrentApp')
    .factory('Tracker', function ($resource, DateUtils) {
        return $resource('api/trackers/:id', {}, {
            query: { method: 'GET', isArray: true},
            get: {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    data.dateLastSync = DateUtils.convertDateTimeFromServer(data.dateLastSync);
                    return data;
                }
            },
            update: { method:'PUT' }
        });
    });
