'use strict';

angular.module('infinitetorrentApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('tracker', {
                parent: 'entity',
                url: '/trackers',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'Trackers'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/tracker/tracker.html',
                        controller: 'TrackerController'
                    }
                },
                resolve: {
                }
            })
            .state('tracker.detail', {
                parent: 'entity',
                url: '/tracker/{id}',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'Tracker'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/tracker/tracker-detail.html',
                        controller: 'TrackerDetailController'
                    }
                },
                resolve: {
                    entity: ['$stateParams', 'Tracker', function($stateParams, Tracker) {
                        return Tracker.get({id : $stateParams.id});
                    }]
                }
            })
            .state('tracker.new', {
                parent: 'tracker',
                url: '/new',
                data: {
                    roles: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$modal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/entities/tracker/tracker-dialog.html',
                        controller: 'TrackerDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {name: null, id: null};
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('tracker', null, { reload: true });
                    }, function() {
                        $state.go('tracker');
                    })
                }]
            })
            .state('tracker.edit', {
                parent: 'tracker',
                url: '/{id}/edit',
                data: {
                    roles: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$modal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/entities/tracker/tracker-dialog.html',
                        controller: 'TrackerDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['Tracker', function(Tracker) {
                                return Tracker.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('tracker', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
