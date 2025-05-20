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

router.beforeEach(async (to, from, next) => {
  const isAuthenticated = await checkAuth();
  const userData = JSON.parse(localStorage.getItem("user")) || {};
  const userRole = userData.role;

  if (!to.meta.requiresAuth) {
    if (isAuthenticated && (to.path === "/login" || to.path === "/register")) {
      return next(userRole === "admin" ? "/admin/home" : "/user/home");
    }
    return next();
  }

  if (!isAuthenticated) {
    if (to.path !== "/login") {
      localStorage.setItem("redirectPath", to.fullPath);
    }
    return next("/login");
  }

  if (to.meta.role !== null && userRole !== to.meta.role) {
    ElMessage.warning("您没有访问该页面的权限");
    return next(userRole === "admin" ? "/admin/home" : "/user/home");
  }

  if (to.path === "/profile") {
    if (!userData.userId) {
      return next("/login");
    }
    to.params.userId = userData.userId;
  }

  next();
});

// 认证检查函数
async function checkAuth() {
  try {
    const user = JSON.parse(localStorage.getItem("user"));
    if (!user?.token) return false;
    return true;
  } catch (error) {
    return false;
  }
}

export default router;
