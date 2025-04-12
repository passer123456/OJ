import { createRouter, createWebHistory } from "vue-router";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/user",
      component: () => import("../views/User.vue"),
      children: [
        {
          path: "problemlist",
          component: () => import("../views/ProblemList.vue"),
        },
        { path: "home", component: () => import("../views/Home.vue") },
      ],
    },
    {
      path: "/admin",
      component: () => import("../views/Admin.vue"),
      children: [
        {
          path: "problemsmanage",
          component: () => import("../views/ProblemsManage.vue"),
        },
        { path: "home", component: () => import("../views/Home.vue") },
      ],
    },
    {
      path: "/problem/:id",
      component: () => import("../views/ProblemDetail.vue"),
      props: true,
    },
  ],
});

export default router;
