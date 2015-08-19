'use strict';

angular.module('infinitetorrentApp')
    .controller('TorrentController', function ($scope, Torrent, ParseLinks) {
        $scope.torrents = [];
        $scope.page = 1;
        $scope.loadAll = function() {
            Torrent.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                for (var i = 0; i < result.length; i++) {
                    $scope.torrents.push(result[i]);
                }
            });
        };
        $scope.reset = function() {
            $scope.page = 1;
            $scope.torrents = [];
            $scope.loadAll();
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.delete = function (id) {
            Torrent.get({id: id}, function(result) {
                $scope.torrent = result;
                $('#deleteTorrentConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Torrent.delete({id: id},
                function () {
                    $scope.reset();
                    $('#deleteTorrentConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.download = function (id) {
            window.location = 'api/torrents/download/' + id;
        };

        $scope.refresh = function () {
            $scope.reset();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.torrent = {name: null, comment: null, created: null, createdBy: null, totalSize: null, file: null, id: null};
        };

        $scope.abbreviate = function (text) {
            if (!angular.isString(text)) {
                return '';
            }
            if (text.length < 30) {
                return text;
            }
            return text ? (text.substring(0, 15) + '...' + text.slice(-10)) : '';
        };

        $scope.byteSize = function (base64String) {
            if (!angular.isString(base64String)) {
                return '';
            }
            function endsWith(suffix, str) {
                return str.indexOf(suffix, str.length - suffix.length) !== -1;
            }
            function paddingSize(base64String) {
                if (endsWith('==', base64String)) {
                    return 2;
                }
                if (endsWith('=', base64String)) {
                    return 1;
                }
                return 0;
            }
            function size(base64String) {
                return base64String.length / 4 * 3 - paddingSize(base64String);
            }
            function formatAsBytes(size) {
                return size.toString().replace(/\B(?=(\d{3})+(?!\d))/g, " ") + " bytes";
            }

            return formatAsBytes(size(base64String));
        };
    });
