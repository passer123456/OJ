<template>
  <div>
    <NavBar
      :menu-items="adminMenuItems"
      @notice-click="handleNotice"
      @logout="handleLogout"
    />
    <div class="workspace">
      <RouterView />
    </div>
  </div>
</template>

<script setup>
import NavBar from "@/components/NavBar.vue";
import { ElMessage } from "element-plus";

const adminMenuItems = [
  { index: "/admin/home", label: "首页" },
  { index: "/admin/problemsmanage", label: "题库管理" },
  { index: "/admin/usermanage", label: "用户管理" },
  { index: "/admin/notice", label: "公告发布" },
];

const handleNotice = () => {
  ElMessage.info("这是公告");
};

const handleLogout = () => {
  // 处理退出登录逻辑
};
</script>
