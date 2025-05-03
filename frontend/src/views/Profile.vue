<template>
  <div class="profile-container">
    <!-- 顶部导航栏 -->
    <div class="header">
      <!-- 左侧标题 -->
      <div class="header-title">个人中心</div>

      <!-- 中间菜单 -->
      <el-menu
        :default-active="activeTab"
        class="header-menu"
        mode="horizontal"
        @select="handleMenuSelect"
      >
        <el-menu-item index="profile">个人信息</el-menu-item>
        <el-menu-item index="password">修改密码</el-menu-item>
        <el-menu-item index="settings">设置</el-menu-item>
      </el-menu>

      <!-- 右侧返回按钮 -->
      <div class="header-actions">
        <el-button type="primary" @click="goBack">
          <el-icon><ArrowLeft /></el-icon>
          返回{{ user.role === "admin" ? "管理" : "" }}界面
        </el-button>
      </div>
    </div>

    <!-- 内容区域 -->
    <div class="profile-content" v-if="activeTab === 'profile'">
      <div class="profile-header">
        <h2>个人信息</h2>
        <button v-if="!isEditing" @click="startEditing" class="edit-button">
          <el-icon><Edit /></el-icon> 编辑
        </button>
      </div>

      <div class="profile-details">
        <!-- 头像区域 -->
        <div class="avatar-section">
          <div class="avatar-wrapper">
            <img
              :src="user.avatar || defaultAvatar"
              alt="用户头像"
              class="avatar-image"
            />
            <div
              v-if="isEditing"
              class="avatar-overlay"
              @click="triggerFileInput"
            >
              <el-icon><Camera /></el-icon>
              <span>更换头像</span>
            </div>
          </div>
          <input
            type="file"
            ref="avatarInput"
            accept="image/*"
            @change="handleAvatarChange"
            class="file-input"
          />
        </div>

        <!-- 用户信息 -->
        <div class="info-section">
          <div class="info-item">
            <label>用户ID</label>
            <div class="info-value">{{ user.userId }}</div>
          </div>

          <div class="info-item">
            <label>用户名</label>
            <el-input
              v-if="isEditing"
              v-model="editForm.username"
              type="text"
              class="info-input"
            />
            <div v-else class="info-value">{{ user.username }}</div>
          </div>

          <div class="info-item">
            <label>角色</label>
            <div class="info-value">{{ user.role }}</div>
          </div>

          <div class="info-item bio-item">
            <label>个人简介</label>
            <el-input
              v-if="isEditing"
              v-model="editForm.bio"
              type="textarea"
              class="bio-textarea"
              :rows="4"
            />
            <div v-else class="bio-content">{{ user.bio || "暂无简介" }}</div>
          </div>
        </div>
      </div>

      <!-- 编辑模式按钮 -->
      <div v-if="isEditing" class="action-buttons">
        <el-button type="primary" @click="saveChanges">
          <el-icon><Check /></el-icon> 保存
        </el-button>
        <el-button @click="cancelEditing">
          <el-icon><Close /></el-icon> 取消
        </el-button>
      </div>
    </div>

    <!-- 其他标签页内容 -->
    <div v-else-if="activeTab === 'password'" class="tab-content">
      <h2>修改密码</h2>
      <!-- 这里放置修改密码的表单 -->
      <el-form
        ref="passwordForm"
        :model="passwordForm"
        :rules="passwordRules"
        label-width="120px"
        class="password-form"
      >
        <el-form-item label="原密码" prop="oldPassword">
          <el-input
            v-model="passwordForm.oldPassword"
            type="password"
            show-password
            placeholder="请输入当前密码"
          />
        </el-form-item>

        <el-form-item label="新密码" prop="newPassword">
          <el-input
            v-model="passwordForm.newPassword"
            type="password"
            show-password
            placeholder="请输入新密码"
          />
        </el-form-item>

        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="passwordForm.confirmPassword"
            type="password"
            show-password
            placeholder="请再次输入新密码"
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            @click="submitPassword"
            :loading="isSubmitting"
          >
            确认修改
          </el-button>
          <el-button @click="resetPasswordForm">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div v-else-if="activeTab === 'settings'" class="tab-content">
      <h2>设置</h2>
      <!-- 这里放置设置内容 -->
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import { useRouter } from "vue-router";
import { ArrowLeft, Edit, Camera, Check, Close } from "@element-plus/icons-vue";
import axios from "@/utils/filterAxios.js";

const router = useRouter();
const defaultAvatar =
  "https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png";

// 用户数据和状态
const user = reactive({
  userId: "",
  username: "",
  role: "",
  avatar: "",
  bio: "",
});

const activeTab = ref("profile");
const isEditing = ref(false);
const avatarInput = ref(null);

// 编辑表单
const editForm = reactive({
  username: "",
  bio: "",
  avatarFile: null,
});

// 获取用户数据
const fetchUserData = async () => {
  try {
    const response = await axios.get(
      `/user/selectById/${JSON.parse(localStorage.getItem("user")).userId}`
    );
    Object.assign(user, response.data);
  } catch (error) {
    console.error("获取用户信息失败:", error);
  }
};

// 初始化
onMounted(fetchUserData);

// 导航菜单处理
const handleMenuSelect = (index) => {
  activeTab.value = index;
};

// 返回上一页
const goBack = () => {
  router.go(-1);
};

// 编辑相关方法
const startEditing = () => {
  editForm.username = user.username;
  editForm.bio = user.bio;
  isEditing.value = true;
};

const cancelEditing = () => {
  isEditing.value = false;
};

const saveChanges = async () => {
  try {
    await axios.put("/user/update", {
      userId: user.userId,
      username: editForm.username,
      bio: editForm.bio,
      avatar: user.avatar,
    });

    const localUser = JSON.parse(localStorage.getItem("user"));
    if (localUser) {
      localUser.username = editForm.username;
      localUser.bio = editForm.bio;
      localUser.avatar = user.avatar; // 更新头像
      localStorage.setItem("user", JSON.stringify(localUser));
    }

    isEditing.value = false;
    ElMessage.success("信息更新成功！");

    await fetchUserData(); // 重新获取用户数据以更新界面
  } catch (error) {
    console.error("更新失败:", error);
    ElMessage.error("更新失败，请重试");
  }
};

const handleAvatarChange = (e) => {
  const file = e.target.files[0];
  if (file) {
    editForm.avatarFile = file;
    user.avatar = URL.createObjectURL(file);
  }
};

const triggerFileInput = () => {
  avatarInput.value.click();
};

// 密码修改相关
// 密码表单数据
const isSubmitting = ref(false);
const passwordForm = reactive({
  oldPassword: "",
  newPassword: "",
  confirmPassword: "",
});

// 表单验证规则
const passwordRules = {
  oldPassword: [
    { required: true, message: "请输入原密码", trigger: "blur" },
    { min: 3, max: 18, message: "长度在3到18个字符", trigger: "blur" },
  ],
  newPassword: [
    { required: true, message: "请输入新密码", trigger: "blur" },
    { min: 3, max: 18, message: "长度在3到18个字符", trigger: "blur" },
    {
      validator: (rule, value, callback) => {
        if (value === passwordForm.oldPassword) {
          callback(new Error("新密码不能与原密码相同"));
        } else {
          callback();
        }
      },
      trigger: "blur",
    },
  ],
  confirmPassword: [
    { required: true, message: "请确认新密码", trigger: "blur" },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error("两次输入密码不一致"));
        } else {
          callback();
        }
      },
      trigger: "blur",
    },
  ],
};

// const passwordForm = ref(null);

// 提交密码修改
const submitPassword = () => {
  passwordForm.value.validate(async (valid) => {
    if (!valid) return;

    isSubmitting.value = true;

    try {
      await axios.post("/user/changepassword", {
        userId: user.userId,
        oldPassword: passwordForm.oldPassword,
        newPassword: passwordForm.newPassword,
      });

      ElMessage.success("密码修改成功");
      resetPasswordForm();

      // 可选：修改成功后强制重新登录
      // localStorage.removeItem('user');
      // router.push('/login');
    } catch (error) {
      console.error("密码修改失败:", error);
      let message = "密码修改失败";
      if (error.response?.data?.message) {
        message = error.response.data.message;
      }
      ElMessage.error(message);
    } finally {
      isSubmitting.value = false;
    }
  });
};

// 重置表单
const resetPasswordForm = () => {
  passwordForm.value.resetFields();
};
</script>

<style scoped>
.profile-container {
  background-color: #f5f7fa;
  min-height: 100vh;
}

/* 导航栏样式 */
.header {
  height: 60px;
  background-color: #1e88e5;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.header-title {
  font-size: 20px;
  font-weight: bold;
  color: white;
  min-width: 100px;
}

.header-menu {
  flex: 1;
  display: flex;
  justify-content: center;
  background-color: transparent;
  border-bottom: none;
}

.header-menu .el-menu-item {
  color: white;
  height: 60px;
  line-height: 60px;
  margin: 0 10px;
}

.header-menu .el-menu-item.is-active {
  color: #ffd04b;
  border-bottom: 2px solid #ffd04b;
}

.header-actions {
  min-width: 200px;
  display: flex;
  justify-content: flex-end;
}

/* 内容区域样式 */
.profile-content {
  max-width: 1000px;
  margin: 20px auto;
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.profile-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}

.profile-header h2 {
  margin: 0;
  font-size: 1.5rem;
  color: #333;
}

.edit-button {
  display: flex;
  align-items: center;
  gap: 5px;
}

.profile-details {
  display: flex;
  gap: 30px;
}

.avatar-section {
  flex: 0 0 200px;
}

.avatar-wrapper {
  position: relative;
  width: 160px;
  height: 160px;
  border-radius: 50%;
  overflow: hidden;
  border: 3px solid #f0f2f5;
}

.avatar-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: white;
  cursor: pointer;
  opacity: 0;
  transition: opacity 0.2s;
}

.avatar-wrapper:hover .avatar-overlay {
  opacity: 1;
}

.file-input {
  display: none;
}

.info-section {
  flex: 1;
}

.info-item {
  margin-bottom: 20px;
}

.info-item label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #666;
}

.info-value {
  padding: 10px;
  background-color: #f9f9f9;
  border-radius: 4px;
  color: #333;
}

.bio-content {
  white-space: pre-wrap;
  line-height: 1.6;
}

.action-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.tab-content {
  max-width: 1000px;
  margin: 20px auto;
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

@media (max-width: 768px) {
  .profile-details {
    flex-direction: column;
  }

  .avatar-section {
    display: flex;
    justify-content: center;
    margin-bottom: 20px;
  }
}
</style>
