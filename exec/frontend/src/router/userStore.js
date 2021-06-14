const routesForUserStore = [
  {
    path: '/deal-for-u',
    name: 'userstore',
    component: () => import('@/views/UserStorePage.vue'),
    children: [
      {
        path: 'menu/:storeId',
        name: 'menu',
        component: () => import('@/components/storeForUser/UserStoreMenu.vue'),
      },
      {
        path: 'info/:storeId',
        name: 'info',
        component: () => import('@/components/storeForUser/UserStoreInfo.vue'),
      },
      {
        path: 'timedeal/:storeId',
        name: 'timedeal',
        component: () => import('@/components/storeForUser/UserStoreTimeDeal.vue'),
      },
      {
        path: 'live/:storeId',
        name: 'live',
        component: () => import('@/components/storeForUser/UserStoreLive.vue'),
      },
      {
        path: 'review/:storeId',
        name: 'review',
        component: () => import('@/components/storeForUser/UserStoreReview.vue'),
      },
    ],
  },
];

export default routesForUserStore;
