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
  // 1. 检查用户认证状态
  const isAuthenticated = await checkAuth();
  const userData = JSON.parse(localStorage.getItem("user")) || {};
  const userRole = userData.role;

  // 2. 处理公开路由（无需认证）
  if (!to.meta.requiresAuth) {
    // 已登录用户访问登录/注册页时重定向到主页
    if (isAuthenticated && (to.path === "/login" || to.path === "/register")) {
      return next(userRole === "admin" ? "/admin/home" : "/user/home");
    }
    return next();
  }

  // 3. 未认证用户访问需认证路由
  if (!isAuthenticated) {
    // 保存目标路由以便登录后重定向
    if (to.path !== "/login") {
      localStorage.setItem("redirectPath", to.fullPath);
    }
    return next("/login");
  }

  // 4. 检查角色权限
  if (to.meta.role !== null && userRole !== to.meta.role) {
    // 管理员尝试访问用户路由或反之
    ElMessage.warning("您没有访问该页面的权限");
    return next(userRole === "admin" ? "/admin/home" : "/user/home");
  }

  // 5. 特殊路由处理
  if (to.path === "/profile") {
    // 确保个人资料页有用户数据
    if (!userData.userId) {
      return next("/login");
    }
    to.params.userId = userData.userId; // 自动注入用户ID
  }

  // 6. 默认放行
  next();
});

// 认证检查函数
async function checkAuth() {
  try {
    const user = JSON.parse(localStorage.getItem("user"));
    if (!user?.token) return false;

    // 可选：验证token有效性（API请求）
    // const { valid } = await axios.get('/auth/check');
    // return valid;

    return true;
  } catch (error) {
    return false;
  }
}

export default router;
