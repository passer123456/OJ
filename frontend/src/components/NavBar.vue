<!-- src/components/NavBar.vue -->
<template>
  <div class="header">
    <!-- 左侧标题 -->
    <slot name="title">
      <div class="header-title">OJ系统</div>
    </slot>

    <!-- 中间菜单 -->
    <el-menu
      :default-active="activeIndex"
      class="header-menu"
      mode="horizontal"
      router
    >
      <el-menu-item
        v-for="item in menuItems"
        :key="item.index"
        :index="item.index"
        :route="item.route || item.index"
      >
        <template v-if="item.icon">
          <el-icon><component :is="item.icon" /></el-icon>
          <span>{{ item.label }}</span>
        </template>
        <span v-else>{{ item.label }}</span>
      </el-menu-item>
    </el-menu>

    <!-- 右侧功能区 -->
    <div class="header-actions">
      <slot name="search">
        <el-input
          v-if="showSearch"
          placeholder="搜索题目"
          style="width: 200px; margin-right: 10px"
          clearable
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </slot>

      <slot name="actions">
        <el-button v-if="showNotice" type="text" @click="handleNoticeClick">
          <el-icon :size="20"><Bell /></el-icon>
        </el-button>
        <el-dropdown>
          <div class="user-info">
            <el-avatar :size="30" :src="avatarUrl" />
            <span class="username">{{ username }}</span>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="handleProfileClick"
                >个人中心</el-dropdown-item
              >
              <el-dropdown-item @click="handleLogout"
                >退出登录</el-dropdown-item
              >
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </slot>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import { Search, Bell } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";

const props = defineProps({
  menuItems: {
    type: Array,
    required: true,
    default: () => [],
  },
  showSearch: {
    type: Boolean,
    default: true,
  },
  showNotice: {
    type: Boolean,
    default: true,
  },
  avatarUrl: {
    type: String,
    default: "",
  },
  username: {
    type: String,
    default: "",
  },
});

const emit = defineEmits(["notice-click", "profile", "logout"]);

const route = useRoute();
const router = useRouter();
const activeIndex = ref(route.path);

// 计算属性获取用户名
const username = computed(() => {
  if (props.username) return props.username;
  const user = JSON.parse(localStorage.getItem("user")) || {
    username: "未登录",
  };
  return user?.username || "未登录";
});

// 计算属性获取头像URL
const avatarUrl = computed(() => {
  if (props.avatarUrl) return props.avatarUrl;
  const user = JSON.parse(localStorage.getItem("user")) || {};
  console.log(user.avatar);
  return (
    user?.avatar ||
    "https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png"
  );
});

// 处理个人中心点击
const handleProfileClick = () => {
  emit("profile");
  router.push("/profile"); // 跳转到个人中心页面
};

//登出
const handleLogout = () => {
  localStorage.removeItem("user");
  emit("logout");
  location.href = "/login";
};

watch(
  () => route.path,
  (path) => {
    activeIndex.value = path;
  }
);

const handleNoticeClick = () => {
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
.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
}
.username {
  margin-left: 8px;
  color: white;
}

/* 工作区域 */
.workspace {
  padding: 20px;
}
</style>
