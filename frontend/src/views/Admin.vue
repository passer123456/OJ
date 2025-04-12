<template>
  <div>
    <!-- 头部导航栏 -->
    <div class="header">
      <!-- 左侧标题 -->
      <div class="header-title">OJ系统</div>

      <!-- 中间菜单 -->
      <el-menu
        :default-active="activeIndex"
        class="header-menu"
        mode="horizontal"
        @select="handleSelect"
        router
      >
        <el-menu-item index="home">首页</el-menu-item>
        <el-menu-item index="problemsmanage">题库管理</el-menu-item>
        <el-menu-item index="usermanage">用户管理</el-menu-item>
        <el-menu-item index="notice">公告发布</el-menu-item>
      </el-menu>

      <!-- 右侧功能区 -->
      <div class="header-actions">
        <el-input
          placeholder="搜索题目"
          style="width: 200px; margin-right: 10px"
          clearable
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-button type="text" @click="showNotice">
          <el-icon :size="20"><Bell /></el-icon>
        </el-button>
        <el-dropdown>
          <span class="user-avatar">
            <el-avatar
              :size="30"
              src="https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png"
            />
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item>个人中心</el-dropdown-item>
              <el-dropdown-item>退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>

    <!-- 工作区域 -->
    <div class="workspace"><RouterView /></div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { Search, Bell } from "@element-plus/icons-vue";

const activeIndex = ref("home");
const handleSelect = (index) => {
  activeIndex.value = index;
};

const showNotice = () => {
  ElMessage.info("这是公告");
};
</script>

<style scoped>
/* 头部布局 */
.header {
  height: 60px;
  background-color: #1e88e5; /* 蓝色背景 */
  display: flex;
  justify-content: space-between; /* 三部分均分空间 */
  align-items: center;
  padding: 0 20px;
  border-bottom: 1px solid #eee;
}

/* 左侧标题 */
.header-title {
  font-size: 20px;
  font-weight: bold;
  color: white;
  min-width: 100px; /* 固定标题宽度 */
}

/* 中间菜单 */
.header-menu {
  flex: 1; /* 占据剩余空间 */
  display: flex;
  justify-content: center; /* 菜单居中 */
  background-color: transparent;
  font-weight: bold;
  border-bottom: none;
}
.header-menu .el-menu-item {
  color: white;
  height: 60px;
  line-height: 60px;
  margin: 0 10px; /* 增加菜单项间距 */
}
.header-menu .el-menu-item.is-active {
  color: #ffd04b;
  border-bottom: 2px solid #ffd04b;
}

/* 右侧功能区 */
.header-actions {
  display: flex;
  align-items: center;
  min-width: 300px; /* 防止挤压 */
  justify-content: flex-end;
}
.user-avatar {
  margin-left: 10px;
  cursor: pointer;
}

/* 工作区域 */
.workspace {
  padding: 20px;
}
</style>
