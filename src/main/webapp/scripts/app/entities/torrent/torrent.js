'use strict';

angular.module('infinitetorrentApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('torrent', {
                parent: 'entity',
                url: '/torrents',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'Torrents'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/torrent/torrents.html',
                        controller: 'TorrentController'
                    }
                },
                resolve: {
                }
            })
            .state('torrent.detail', {
                parent: 'entity',
                url: '/torrent/{id}',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'Torrent'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/torrent/torrent-detail.html',
                        controller: 'TorrentDetailController'
                    }
                },
                resolve: {
                    entity: ['$stateParams', 'Torrent', function($stateParams, Torrent) {
                        return Torrent.get({id : $stateParams.id});
                    }]
                }
            })
            .state('torrent.new', {
                parent: 'torrent',
                url: '/new',
                data: {
                    roles: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$modal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/entities/torrent/torrent-dialog.html',
                        controller: 'TorrentDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {name: null, comment: null, created: null, createdBy: null, totalSize: null, file: null, id: null, leechers: null, seeders: null};
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('torrent', null, { reload: true });
                    }, function() {
                        $state.go('torrent');
                    })
                }]
            })
    });
