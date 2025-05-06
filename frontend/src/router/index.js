import { createRouter, createWebHistory } from "vue-router";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/",
      redirect: "/login",
    },
    {
      path: "/login",
      name: "Login",
      component: () => import("../views/Login.vue"),
      meta: { requiresAuth: false },
    },
    {
      path: "/register",
      name: "Register",
      component: () => import("../views/Register.vue"),
      meta: { requiresAuth: false },
    },
    {
      path: "/user",
      component: () => import("../views/User.vue"),
      meta: { requiresAuth: true, role: "user" },
      children: [
        {
          path: "problemlist",
          component: () => import("../views/ProblemList.vue"),
          meta: { requiresAuth: true, role: "user" },
        },
        {
          path: "home",
          component: () => import("../views/Home.vue"),
          meta: { requiresAuth: true, role: "user" },
        },
        {
          path: "submitrecord",
          component: () => import("../views/SubmitRecordList.vue"),
          meta: { requiresAuth: true, role: "user" },
        },
      ],
    },
    {
      path: "/admin",
      component: () => import("../views/Admin.vue"),
      meta: { requiresAuth: true, role: "admin" },
      children: [
        {
          path: "problemsmanage",
          component: () => import("../views/ProblemsManage.vue"),
          meta: { requiresAuth: true, role: "admin" },
        },
        {
          path: "home",
          component: () => import("../views/Home.vue"),
          meta: { requiresAuth: true, role: "admin" },
        },
        {
          path: "usermanage",
          component: () => import("../views/UserManage.vue"),
          meta: { requiresAuth: true, role: "admin" },
        },
      ],
    },
    {
      path: "/problem/:id",
      component: () => import("../views/ProblemDetail.vue"),
      props: true,
      meta: {
        requiresAuth: true,
        role: null, // 设置为 null 表示允许所有角色
      },
    },
    {
      path: "/profile",
      name: "Profile",
      component: () => import("../views/Profile.vue"),
      props: true,
      meta: {
        requiresAuth: true,
        role: null, // 设置为 null 表示允许所有角色
      },
    },
    {
      path: "/:pathMatch(.*)*",
      redirect: "/login",
    },
  ],
});

// router.beforeEach(async (to, from, next) => {
//   const isAuthenticated = await checkAuth();
//   const userRole = localStorage.getItem("role");

//   // 未认证且需要认证的路由
//   if (to.meta.requiresAuth && !isAuthenticated) {
//     return next("/login");
//   }

//   // 已认证但角色不匹配
//   if (to.meta.requiresAuth && to.meta.role && userRole !== to.meta.role) {
//     // 检查是否有匹配的子路由
//     if (to.matched.some((record) => record.meta.role === userRole)) {
//       return next();
//     }
//     return next(userRole === "admin" ? "/admin/home" : "/user/home");
//   }

//   next();
// });

export default router;
