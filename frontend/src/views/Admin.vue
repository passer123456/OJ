<template>
  <div>
    <NavBar :menu-items="adminMenuItems" />
    <!-- @notice-click="handleNotice" -->
    <div class="workspace">
      <RouterView />
    </div>
  </div>
</template>

<script setup>
import NavBar from "@/components/NavBar.vue";
// import { ElMessage } from "element-plus";
// import { ref, computed, watch } from "vue";

const adminMenuItems = [
  { index: "/admin/home", label: "首页" },
  { index: "/admin/problemsmanage", label: "题库管理" },
  { index: "/admin/usermanage", label: "用户管理" },
  { index: "/admin/notice", label: "公告发布" },
];

// const handleNotice = () => {
//   ElMessage.info("这是公告");
// };

// // 响应式用户数据
// const user = ref(
//   JSON.parse(localStorage.getItem("user")) || { username: "未登录" }
// );

// // 计算属性获取用户名
// const username = computed(() => user.value?.username || "未登录");

// // 自动同步用户数据到localStorage
// watch(
//   user,
//   (newVal) => {
//     localStorage.setItem("user", JSON.stringify(newVal));
//   },
//   { deep: true }
// );

// const handleLogout = () => {
//   // localStorage.removeItem("token");
//   localStorage.removeItem("user");
//   location.href = "/login";
// };

// // 监听storage事件实现跨标签页同步
// window.addEventListener("storage", (event) => {
//   if (event.key === "user") {
//     user.value = JSON.parse(event.newValue) || { username: "未登录" };
//   }
// });
</script>
