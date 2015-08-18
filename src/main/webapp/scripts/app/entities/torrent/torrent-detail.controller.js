'use strict';

angular.module('infinitetorrentApp')
    .controller('TorrentDetailController', function ($scope, $rootScope, $stateParams, entity, Torrent) {
        $scope.torrent = entity;
        $scope.load = function (id) {
            Torrent.get({id: id}, function(result) {
                $scope.torrent = result;
            });
        };
        $rootScope.$on('infinitetorrentApp:torrentUpdate', function(event, result) {
            $scope.torrent = result;
        });
    });
